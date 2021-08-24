package se325.asyncchat.client;

import se325.asyncchat.common.Keyboard;
import se325.asyncchat.domain.Message;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A simple client for the async web service chat program.
 */
public class ClientMain {

    private static final String WEB_SERVICE_URI = "http://localhost:10000/services/chat";

    private final Client subClient;
    private final String username;

    private ClientMain(String username) {
        this.username = username;
        this.subClient = ClientBuilder.newClient();
    }

    private void start() {
        subscribe();
        chatLoop();
    }

    /**
     * Subscribes to be notified when a message is received from any user in the chat.
     */
    private void subscribe() {
        subClient.target(WEB_SERVICE_URI + "/sub").request()
                .accept(MediaType.APPLICATION_JSON)
                .async().get(callback);
    }

    /**
     * The callback to be notified when a chat message is received, or when an error occurs while waiting for a message
     */
    private final InvocationCallback<Message> callback = new InvocationCallback<>() {

        /**
         * Called when a new chat message is received. Displays the received message, then calls subscribe() again,
         * because each call to an async web method only gets one response. If we want to receive more messages, we
         * need to re-sub again.
         *
         * @param message the received message
         */
        @Override
        public void completed(Message message) {
            // Received a message. Display it and re-sub.
            System.out.println(message);
            subscribe();
        }

        /**
         * Called if there's an error while waiting for a chat message. Logs the error, then quits the program.
         *
         * @param throwable the error
         */
        @Override
        public void failed(Throwable throwable) {
            // Failed. Print failure message and close client.
            System.err.println("SUB ERROR: " + throwable.getMessage());
            subClient.close();
            System.exit(0);
        }
    };

    /**
     * Continually reads console input and sends each line of console input to the server as a new message, until
     * the user types QUIT.
     */
    private void chatLoop() {

        Client chatClient = ClientBuilder.newClient();

        try {

            String line;
            while (!(line = Keyboard.prompt("")).equals("QUIT")) {

                Message message = new Message(username, line);

                try (Response response = chatClient.target(WEB_SERVICE_URI).request().post(Entity.json(message))) {
                    if (response.getStatus() != 204) {
                        System.err.println("Response was " + response.getStatus() + " " + response.getStatusInfo() + "!");
                    }
                }
            }
        } catch (Exception ex) {

            System.err.println("CHAT LOOP ERROR: " + ex.getMessage());

        } finally {
            subClient.close();
            chatClient.close();
        }

        System.out.println("Chat program finished");

    }

    public static void main(String[] args) {

        String username = Keyboard.prompt("Enter your username: ");
        new ClientMain(username).start();

    }
}
