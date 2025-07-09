package se325.websocketchat.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.websocketchat.domain.Message;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a server endpoint. The class defines the URL, whereas each instance of the class represents the server's
 * connection to a single client.
 */
@ServerEndpoint(value = "/chat/{username}",
        encoders = {Message.Coder.class},
        decoders = {Message.Coder.class})
public class ChatServiceEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceEndpoint.class);

    /**
     * A place to store all client connections.
     * {@link CopyOnWriteArrayList} is a special kind of List in Java which allows multiple concurrent reads, but
     * thread-safe writes. Useful in apps like this, where we need to read from the list often (everytime a message is
     * sent) but only need to write from it occasionally (when a new user joins or leaves).
     **/
    private static final List<ChatServiceEndpoint> allEndpoints = new CopyOnWriteArrayList<>();

    /**
     * The session representing the client connection for this endpoint
     */
    private Session session;

    /**
     * The username of the client for this endpoint
     */
    private String username;

    /**
     * When a new client connects to the URL above, a new instance of {@link ChatServiceEndpoint} will be created, and
     * its onOpen() method will be called.
     *
     * @param session  the {@link Session} representing the connection to the client
     * @param username the username - taken from the URL path. Similar to JAX-RS path parameters.
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {

        LOGGER.info("Session created: username = " + username + ", session id = " + session.getId());
        this.session = session;
        this.username = username;
        allEndpoints.add(this);
    }

    /**
     * Called when this endpoint receives a message from a client. This simple server will broadcast the message to
     * all clients.
     *
     * @param message the {@link Message} which was sent. Automatically converted from JSON by the configured decoder
     *                above ({@link Message.Coder}).
     */
    @OnMessage
    public void onMessage(Message message) {
        message.setUsername(this.username);
        LOGGER.info("Message received at server: " + message);
        broadcast(message);
    }

    /**
     * Called when the client disconnects. We will remove the reference to this endpoint from the list above.
     */
    @OnClose
    public void onClose() {

        LOGGER.info("Client disconnected. Session id: " + session.getId());

        allEndpoints.remove(this);

        // Send a message saying this user disconnected
        Message message = new Message("Server", username + " disconnected!");
        broadcast(message);
    }

    /**
     * TODO Error handling
     *
     * @param throwable the error
     */
    @OnError
    public void onError(Throwable throwable) {
        // TODO Do error handling here
        LOGGER.error("Error with WebSocket server", throwable);
    }

    /**
     * Sends the given {@link Message} to all chat clients
     *
     * @param message the message to send
     */
    private static void broadcast(Message message) {
        allEndpoints.parallelStream().forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().sendObject(message);
                } catch (IOException | EncodeException e) {
                    // TODO Proper error handling
                    LOGGER.error("Error broadcasting to client endpoint", e);
                }
            }
        });
    }
}
