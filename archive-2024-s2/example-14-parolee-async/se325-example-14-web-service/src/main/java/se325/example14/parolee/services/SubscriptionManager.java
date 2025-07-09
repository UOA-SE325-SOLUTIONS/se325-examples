package se325.example14.parolee.services;

import se325.example14.parolee.domain.Curfew;
import se325.example14.parolee.domain.Movement;
import se325.example14.parolee.domain.Parolee;
import se325.example14.parolee.domain.mappers.GeoPositionMapper;
import se325.example14.parolee.dto.ParoleViolationDTO;
import se325.example14.parolee.utils.GeoUtils;

import javax.persistence.EntityManager;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubscriptionManager {

    private static SubscriptionManager instance;

    public static SubscriptionManager instance() {
        if (instance == null) {
            instance = new SubscriptionManager();
        }
        return instance;
    }

    private SubscriptionManager() {
    }

    private final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private final Map<Long, List<AsyncResponse>> subscriptions = new HashMap<>();

    /**
     * Adds a new subscription to be notified if and when the parolee with the given id violates their parole
     *
     * @param paroleeId the id of the parolee
     * @param sub       the new sub
     */
    public void addSubscription(long paroleeId, AsyncResponse sub) {
        synchronized (subscriptions) {
            if (!this.subscriptions.containsKey(paroleeId)) {
                this.subscriptions.put(paroleeId, new ArrayList<>());
            }

            this.subscriptions.get(paroleeId).add(sub);
        }
    }

    /**
     * Gets all subs interested in a given parolee
     *
     * @param paroleeId the id of the parolee
     * @return an immutable list of subs for that parolee (can be an empty list)
     */
    private List<AsyncResponse> getSubsFor(long paroleeId) {
        synchronized (subscriptions) {
            if (!subscriptions.containsKey(paroleeId)) {
                return List.of();
            }
            return new ArrayList<>(subscriptions.get(paroleeId));
        }
    }

    /**
     * Removes the given sub from the subs list for the given parolee
     *
     * @param paroleeId the id of the parolee
     * @param sub       the sub to remove
     */
    private void removeSubs(long paroleeId, AsyncResponse sub) {
        synchronized (subscriptions) {
            subscriptions.get(paroleeId).remove(sub);
        }
    }

    /**
     * Processes all subs for the parolee with the given id
     *
     * @param paroleeId the parolee id
     */
    public void processSubsFor(final long paroleeId) {
        THREAD_POOL.submit(() -> {
            // Get the subs which will be notified if the parolee with the given id is violating parole.
            List<AsyncResponse> subs = getSubsFor(paroleeId);
            if (!subs.isEmpty()) {
                final ParoleViolationDTO violation = isViolatingParole(paroleeId);
                if (violation != null) {
                    // If a parole violation occurred, notify all subs. When a sub has been notified, it will remove
                    // itself from the list.
                    subs.parallelStream().forEach(sub -> {
                        sub.resume(Response.ok(violation).build());
                        removeSubs(paroleeId, sub);
                    });
                }
            }
        });
    }

    /**
     * Gets a value indicating whether the parolee with the given id is violating parole. A parole violation occurs if
     * a parolee has a curfew, and the parolee's last known position is outside that curfew's confinement radius.
     *
     * @param paroleeId the id of the parolee to check.
     * @return A {@link ParoleViolationDTO} instance with the parolee id and the violating location, or null
     */
    private ParoleViolationDTO isViolatingParole(long paroleeId) {

        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();

            // If parolee doesn't exist, they can't be breaking curfew.
            Parolee parolee = em.find(Parolee.class, paroleeId);
            if (parolee == null) {
                em.getTransaction().rollback();
                return null;
            }

            // If a parolee doesn't have a last known position, we can't check if they're breaking curfew.
            // If they don't have a curfew, they can't be breaking it!
            Curfew curfew = parolee.getCurfew();
            Movement lastKnownMovement = parolee.getLastKnownPosition();
            if (lastKnownMovement == null || curfew == null) {
                em.getTransaction().rollback();
                return null;
            }

            // Work out whether the movement occurred within the curfew time. If not, then this isn't a problem.
            LocalDate movementDate = lastKnownMovement.getTimestamp().toLocalDate();
            LocalDateTime curfewStart = LocalDateTime.of(movementDate, curfew.getStartTime());
            LocalDateTime curfewEnd = LocalDateTime.of(movementDate, curfew.getEndTime());
            if (curfewEnd.isBefore(curfewStart)) {
                curfewEnd = curfewEnd.plusDays(1);
            }

            boolean withinCurfewTime = (lastKnownMovement.getTimestamp().isAfter(curfewStart) &&
                    lastKnownMovement.getTimestamp().isBefore(curfewEnd));

            // Curfew is broken if the movement occurred within the curfew time, and is outside
            // the curfew's radius
            if (withinCurfewTime &&
                    GeoUtils.calculateDistanceInMeters(curfew.getConfinementLocation(),
                            lastKnownMovement.getGeoPosition()) > curfew.getConfinementRadius()) {

                ParoleViolationDTO violation = new ParoleViolationDTO(paroleeId,
                        GeoPositionMapper.toDTO(lastKnownMovement.getGeoPosition()));
                em.getTransaction().commit();
                return violation;
            }

            // No violation
            em.getTransaction().rollback();
            return null;

        } finally {
            em.close();
        }
    }
}
