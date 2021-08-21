package se325.asyncchat.client;

import se325.asyncchat.common.Keyboard;
import se325.asyncchat.domain.Message;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ClientMain {

    private static final String WEB_SERVICE_URI = "http://localhost:10000/services/chat";

    private final String username;

    private ClientMain(String username) {
        this.username = username;

    }

    private void start() {

        subscribe();

        chatLoop();
    }

    private void subscribe() {
        subscribe(ClientBuilder.newClient());
    }

    private void subscribe(final Client subClient) {

        subClient.target(WEB_SERVICE_URI + "/sub")
                .request().accept(MediaType.APPLICATION_JSON).async().get(new InvocationCallback<Message>() {

            @Override
            public void completed(Message message) {
                // Received a message. Display it and re-sub.
                System.out.println(message);
                subscribe(subClient);
            }

            @Override
            public void failed(Throwable throwable) {
                // Failed. Print failure message and close client.
                System.err.println("SUB ERROR: " + throwable.getMessage());
                subClient.close();
            }
        });

    }

    private void chatLoop() {

        Client chatClient = ClientBuilder.newClient();

        try {

            while (true) {

                String msg = Keyboard.prompt("");

                Message message = new Message(username, msg);

                Response response = chatClient.target(WEB_SERVICE_URI).request().post(Entity.json(message));
                if (response.getStatus() != 200) {
                    System.err.println("Response was " + response.getStatusInfo() + "!");
                }

            }
        } catch (Exception ex) {

            System.err.println("CHAT LOOP ERROR: " + ex.getMessage());

        } finally {

            chatClient.close();
        }

        System.out.println("Chat program finished");

    }

    public static void main(String[] args) {

        String username = Keyboard.prompt("Enter your username: ");
        new ClientMain(username).start();

    }
}
