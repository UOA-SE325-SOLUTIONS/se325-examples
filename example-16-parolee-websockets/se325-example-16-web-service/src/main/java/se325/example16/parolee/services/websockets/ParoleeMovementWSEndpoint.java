package se325.example16.parolee.services.websockets;

import se325.example16.parolee.dto.MovementDTO;
import se325.example16.parolee.dto.ParoleeMovementNotificationDTO;
import se325.example16.parolee.dto.ParoleeMovementSubscriptionDTO;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * A WebSocket endpoint which will publish all parolee movements to those parties who are interested in them.
 */
@ServerEndpoint(value = "/ws/parolee-movements/",
        encoders = {ParoleeMovementNotificationDTO.Coder.class},
        decoders = {ParoleeMovementSubscriptionDTO.Coder.class})
public class ParoleeMovementWSEndpoint {

    private Session session;
    private ParoleeMovementSubscriptionDTO subInfo;

    /**
     * Called just after an instance of this class is instantiated by the WebSocket API, to act as the endpoint for
     * a particular client. Registers this endpoint with our {@link ParoleeMovementSubscriptionManager}.
     *
     * @param session the {@link Session} we can use to communicate with the client
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        ParoleeMovementSubscriptionManager.instance().registerEndpoint(this);
    }

    /**
     * Called when the client sends us a message. That message should be a {@link ParoleeMovementSubscriptionDTO}
     * instance, which is used to determine which parolees the client is interested in.
     *
     * @param subInfo info about the parolees the sending client is interested in
     */
    @OnMessage
    public void configure(ParoleeMovementSubscriptionDTO subInfo) {
        this.subInfo = subInfo;
    }

    /**
     * Called when the client closes the connection. De-register ourselves from the sub manager.
     */
    @OnClose
    public void onClose() {
        ParoleeMovementSubscriptionManager.instance().deregisterEndpoint(this);
    }

    /**
     * To be called by the {@link ParoleeMovementSubscriptionManager} to notify the client at this endpoint of a
     * movement. This method will forward it on, if this endpoint's client is interested in that parolee.
     * <p>
     * {@link Session#getAsyncRemote()} is used so we don't block the sub manager waiting to send to this client.
     *
     * @param notification the notification, containing the id of the parolee which moved, along with the
     *                     {@link MovementDTO} describing the movement.
     */
    public void notifyMovement(ParoleeMovementNotificationDTO notification) {
        if (this.subInfo != null && this.subInfo.isInterestedIn(notification.getParoleeId())) {
            this.session.getAsyncRemote().sendObject(notification);
        }
    }
}
