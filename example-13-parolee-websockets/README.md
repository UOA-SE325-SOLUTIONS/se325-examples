# SE325 Example - Parolees with Web Sockets

This project shows how we can use Spring Web Sockets to enable WebSocket communication in our SpringBoot apps. In this case, a user can open a WebSocket channel to receive movement updates on all parolees.

## The app

The STOMP / WebSocket functionality in our server is configured (in `WebSocketConfig`) similarly to [the previous example](../example-12-chat-stomp-websockets). This time, we don't have the ability for clients to _publish_ via the WebSocket - we only allow new parolee movements to come in via our REST endpoint. However, we can still broadcast to all WS clients using the `SimpMessagingTemplate` autowired bean. We are doing this in `MovementBroadcastService`, which is used in our `ParoleeController`'s `createMovementForParolee()` method.

## The tests

The integration test for the websocket functionality can be seen in `MovementWebSocketIT`. Here, we are creating a new STOMP / WebSocket client, and listening for messages on `/topic/movements`, from our server (which is actually hosted on `localhost:<random_port>` this time, rather than using Mock Mvc). In our test method, we are sending a `POST` request containing new movement data to `/parolees/{id}`, and then checking our messages queue to make sure the new movement was broadcast properly.
