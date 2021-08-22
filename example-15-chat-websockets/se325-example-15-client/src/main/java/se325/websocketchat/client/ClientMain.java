package se325.websocketchat.client;

import se325.websocketchat.common.Keyboard;
import se325.websocketchat.domain.Message;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint(
        encoders = {Message.Coder.class},
        decoders = {Message.Coder.class}
)
public class ClientMain {

    private static final String WEB_SERVICE_URI = "ws://localhost:10000/chat/";

    private final String username;

    public ClientMain(String username) {
        this.username = username;
    }

    public void start() throws URISyntaxException, DeploymentException, IOException, EncodeException {

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try (Session mySession = container.connectToServer(this, new URI(WEB_SERVICE_URI + username))) {
            chatLoop(mySession);
        }
    }

    private void chatLoop(Session mySession) throws IOException, EncodeException {
        String line;
        while (!(line = Keyboard.prompt("")).equals("QUIT")) {

            Message message = new Message(line);
            mySession.getBasicRemote().sendObject(message);

        }
    }

//    @OnOpen
//    public void onOpen(Session session) {
//        System.out.println("WebSocket opened: " + session.getId());
//    }

    @OnMessage
    public void onMessage(Message message) {
        System.out.println(message);
    }

    public static void main(String[] args) throws URISyntaxException, DeploymentException, IOException, EncodeException {

        String username = Keyboard.prompt("Enter your username: ");
        new ClientMain(username).start();
    }
}
