# SE325 Example - Chat webapp with Spring WebSockets / STOMP

A simple chat service which uses STOMP over WebSockets to send text-based (JSON) messages bi-directionally.

## The server

1. Clients can connect to `ws://<host>:<port>/chat`, as defined on `WebSocketConfig` line 21 / 22

2. Clients can publish text messages to `/app/chat`:
   - `/app` as configured by `WebSocketConfig` line 16
   - `/app/chat` as configured in the controller, see the next point below.

3. These messages are parsed as JSON and end up at `MessageWebSocketController`'s `send()` method, according to the `@MessageMapping("/chat")` annotation.

4. The new message is converted to a `Message` entity class and saved to the database in the `MessageService`, which in-turn uses `MessageRepository` to do the saving.

5. The message entity (including `id` and `sentAt` in addition to the `sender` and `content` already there) is returned from the controller's `send()` method, which is automatically broadcast to all clients subscribed to `/topic/messages`, as defined by the `@SendTo` annotation.

6. There is also a REST endpoint at `GET /messages`, which will return all messages, ordered by timestamp, most recent first. This is configured in `MessageRestController`.

7. Finally, we have a REST endpoint at `POST /messages/broadcast` which will broadcast the incoming message to all connected WebSocket clients. This functionality is handled in `MessageService`, using an autowired `SimpMessagingTemplate` instance. This is how we can programmatically send messages to WebSocket clients, without waiting for one of them to publish something to us first.

## The client

A web-based (HTML / CSS / JavaScript) client is provided in `src/main/resources/static`. The logic is in `app.js`.

1. The `ChatApp` ES6 class contains methods which will add event listeners to various DOM elements in the HTML, such as the "connect", "disconnect", and "send" buttons, along with the message input box and messages display area.

2. The WebSocket-related code is in the `connect()` method. it will creat a new STOMP client listening over a WebSocket pointing to the host / port / path mentioned above, and will subscribe to `/topic/messages`. When a message is received here, it will be displayed on the webpage.

3. In the `sendMessage()` method, we're reading the text input from the user, and publishing it as a JSON string to the `/app/chat` endpoint.
