package se325.example16.parolee.services;


import org.junit.BeforeClass;
import org.junit.Test;
import se325.example16.parolee.dto.GeoPositionDTO;
import se325.example16.parolee.dto.MovementDTO;
import se325.example16.parolee.dto.ParoleeMovementNotificationDTO;
import se325.example16.parolee.dto.ParoleeMovementSubscriptionDTO;

import javax.websocket.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ParoleeWebSocketsIT extends BaseIntegrationTests {

    private static final String WEB_SOCKET_URI = "ws://localhost:10000/ws/parolee-movements/";

    private static WebSocketContainer container;

    @BeforeClass
    public static void createWebSocketContainer() {
        container = ContainerProvider.getWebSocketContainer();
    }

    /**
     * Tests that we don't get movement notifications when we haven't sent any subscription data to the server.
     */
    @Test
    public void testSubscribeToMovements_NoSubData() throws Exception {
        TestEndpoint ep = new TestEndpoint();
        try (Session session = container.connectToServer(ep, new URI(WEB_SOCKET_URI))) {

            // Move parolee 1
            addParoleeMovement(1, 12, 0, 100, 100);

            // Wait two seconds to see if any messages show up. They shouldn't.
            assertFalse(waitForNumMessages(ep, 1, 2000));

        }
    }

    /**
     * Tests that we don't get movement notifications when we are subscribed to a different parolee.
     */
    @Test
    public void testSubscribeToMovements_DifferentParolee() throws Exception {
        TestEndpoint ep = new TestEndpoint();
        try (Session session = container.connectToServer(ep, new URI(WEB_SOCKET_URI))) {

            // Subscribe to be notified of movements of parolee 2
            session.getBasicRemote().sendObject(new ParoleeMovementSubscriptionDTO(2L));

            // Wait for the sub to go through.
            Thread.sleep(500);

            // Move parolee 1
            addParoleeMovement(1, 12, 0, 100, 100);

            // Wait two seconds to see if any messages show up. They shouldn't.
            assertFalse(waitForNumMessages(ep, 1, 2000));
        }
    }

    /**
     * Tests that we get movement notifications when we are subscribed to the correct parolee.
     */
    @Test
    public void testSubscribeToMovements() throws Exception {
        TestEndpoint ep = new TestEndpoint();
        try (Session session = container.connectToServer(ep, new URI(WEB_SOCKET_URI))) {

            // Subscribe to be notified of movements of parolee 1
            session.getBasicRemote().sendObject(new ParoleeMovementSubscriptionDTO(1L));

            // Wait for the sub to go through.
            Thread.sleep(500);

            // Move parolee 1
            addParoleeMovement(1, 12, 0, 100, 100);

            // Wait two seconds to see if any messages show up. One should show up.
            assertTrue(waitForNumMessages(ep, 1, 2000));

            // Move again
            addParoleeMovement(1, 12, 1, 100, 100);
            addParoleeMovement(1, 12, 2, 100, 100);

            // The list should be up to 3 now.
            assertTrue(waitForNumMessages(ep, 3, 2000));
        }
    }

    /**
     * Adds a new parolee movement.
     *
     * @param paroleeId the id of the parolee to move
     * @param hour      the hour of day
     * @param minute    the minute of day
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    private void addParoleeMovement(long paroleeId, int hour, int minute, double latitude, double longitude) {
        MovementDTO dto = new MovementDTO(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute)),
                new GeoPositionDTO(latitude, longitude));
        try (Response response = client.target(WEB_SERVICE_URI + "/" + paroleeId + "/movements")
                .request().post(Entity.json(dto))) {
            assertEquals(204, response.getStatus());
        }
    }

    /**
     * Waits for the given endpoint to contain at least the given number of messages from the server, for (at max)
     * a given amount of time. Returns a value indicating whether the number of messages was obtained in that time.
     *
     * @param endpoint      the endpoint to check
     * @param numMessages   the number of messages to wait for
     * @param timeoutMillis the amount of time to wait, in millis
     * @return true if the endpoint has at least the requested number of messages after the timeout, false otherwise.
     * @throws InterruptedException if something went wrong.
     */
    private boolean waitForNumMessages(TestEndpoint endpoint, int numMessages, long timeoutMillis) throws InterruptedException {
        synchronized (endpoint.messagesFromServer) {
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;
            while (endpoint.messagesFromServer.size() < numMessages && elapsedTime < timeoutMillis) {
                endpoint.messagesFromServer.wait(timeoutMillis - elapsedTime);

                if (endpoint.messagesFromServer.size() >= numMessages) {
                    return true;
                }

                elapsedTime = System.currentTimeMillis() - startTime;
            }

            return false;
        }
    }

    /**
     * A test endpoint which simply adds received messages to a list.
     */
    @ClientEndpoint(
            encoders = {ParoleeMovementSubscriptionDTO.Coder.class},
            decoders = {ParoleeMovementNotificationDTO.Coder.class}
    )
    public static class TestEndpoint {

        private final List<ParoleeMovementNotificationDTO> messagesFromServer = new ArrayList<>();

        @OnMessage
        public void onMessage(ParoleeMovementNotificationDTO notification) {
            synchronized (messagesFromServer) {
                messagesFromServer.add(notification);

                // Signals anything else waiting on this lock that they can see if they want to continue
                messagesFromServer.notifyAll();
            }
        }
    }
}
