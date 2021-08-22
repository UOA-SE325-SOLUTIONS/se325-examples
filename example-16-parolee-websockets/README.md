# SE325 Example 16 - Parolee service with WebSockets
This project contains a parolee web service which builds on the one in [Example 14](../example-14-parolee-async). All functionality of Example 14 is in this project too. However, we have now added support for users to subscribe to be notified whenever particular parolees move. This subscription service is handled using WebSockets as opposed to async web services.

Because WebSockets are used, clients need only subscribe to notifications *once*. They will then be notified *every time* the parolees they're interested in move around. Clients *do not* need to resubscribe each time.


## The service
In our `se325-examlple-16-common` project, in the `se325.example16.parolee.dto` package, we have two new DTOs:
- `ParoleeMovementSubscriptionDTO`: Intended to be sent by clients to the server, to inform the server which parolees those clients are interested in.
- `ParoleeMovementNotificationDTO`: Intended to be sent by the server to notify clients that a particular parolee has moved. Includes information about the id of the parolee, as well as a `MovementDTO` instance describing the movement itself.

In our `se325-examlple-16-common` project, in the `se325.example16.parolee.services.websockets` package, we see the WebSocket server endpoint: `ParoleeMovementWSEndpoint`. As configured in the `@ServerEndpoint` annotation, clients may open a WebSocket connection to this service using the url `ws://.../ws/parolee-movements/`. Whenever a client connects this way, a new instance of `ParoleeMovementWSEndpoint` will be created, representing that connection. These endpoints are then registered with the `ParoleeMovementSubscriptionManager` class.

As seen in `ParoleeMovementWSEndpoint` on line 42, whenever a server endpoint receives a message, it is interpreted as a `ParoleeMovementSubscriptionDTO` instance, and used to configure that endpoint with the parolees that client is interested in. And, when the client closes the connection, we deregister ourselves with the sub manager on line 50. Finally, the `notifyMovement()` method on line 64 sends the given DTO back to the client, as long as the client is interested in the associated parolee.

`ParoleeMovementSubscriptionManager` is a singleton which manages a collection of endpoints representing the currently-active WebSocket connections from clients. It also contains a method, `notifySubscribers()` (line 43), which can be used by the rest of the system to send movement notifications back to clients.


## The integration tests
The integration tests in class `ParoleeWebSocketsIT` show one way we might test our WebSocket code. Let's focus on test `testSubscribeToMovements()`, which tests that, one a client subscribes, they'll be notified of all movements for their chosen parolees.

On line 151, we've defined a very simple WebSocket client endpoint - `TestEndpoint`. This will simply add all received messages to a list for us to check later. We're using that in `testSubscribeToMovements()` line 78 in order to establish a connection.

On line 81, we're sending a `ParoleeMovementSubscriptionDTO` through the socket, to tell the server which parolees we're interested in. Then, we're sleeping the thread for a bit to make sure the server has had time to process the request (unfortunately, by the time line 81 completes, we can't guarantee that the server has received and finished processing the message).

The rest of the test involves calling the `addParoleeMovement()` method, which sends a normal HTTP request to the server to invoke the appropriate JAX-RS resource method. And, waiting for the expected number of notifications to show up at the client using the `waitForNumMessages()` method. Waiting is achieved using Java's support for concurrency control using [`wait()` and `notify()`](https://www.baeldung.com/java-wait-notify).
