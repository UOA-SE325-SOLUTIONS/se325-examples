package se325.example16.parolee.services.websockets;

import se325.example16.parolee.dto.MovementDTO;
import se325.example16.parolee.dto.ParoleeMovementNotificationDTO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class maintains a collection of {@link ParoleeMovementWSEndpoint} instances, representing WebSocket connections
 * to individual clients who are interested in hearing about the movements of particular parolees.
 */
public class ParoleeMovementSubscriptionManager {

    private static ParoleeMovementSubscriptionManager instance;

    public static ParoleeMovementSubscriptionManager instance() {
        if (instance == null) {
            instance = new ParoleeMovementSubscriptionManager();
        }
        return instance;
    }

    private ParoleeMovementSubscriptionManager() {
    }

    private final List<ParoleeMovementWSEndpoint> endpoints = new CopyOnWriteArrayList<>();

    public void registerEndpoint(ParoleeMovementWSEndpoint endpoint) {
        endpoints.add(endpoint);
    }

    public void deregisterEndpoint(ParoleeMovementWSEndpoint endpoint) {
        endpoints.remove(endpoint);
    }

    /**
     * Notifies all active subscribers of a parolee movement
     *
     * @param paroleeId the id of the parolee which moved
     * @param movement  the movement
     */
    public void notifySubscribers(long paroleeId, MovementDTO movement) {
        final ParoleeMovementNotificationDTO notification = new ParoleeMovementNotificationDTO(paroleeId, movement);
        endpoints.forEach(e -> e.notifyMovement(notification));
    }
}
