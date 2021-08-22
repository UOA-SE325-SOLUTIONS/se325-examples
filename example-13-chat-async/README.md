# SE325 Example 13 - Chat app using Asynchronous web services
This example shows how a rudimentary chat app can be build using JAX-RS support for asynchronous web services. Compare and contrast this with the WebSockets approach in [Example 15](../example-15-chat-websockets).


## Running the app
The server can be run within a web servlet container such as Tomcat, just as with any other JAX-RS service. Follow the instructional video on Canvas if you need a reminder of how to do this. The client code assumes an app context of `/`, but this can be changed by editing `ClientMain` line 15 if desired.

Once the server is running, one instance of the client can be run by running `ClientMain` directly from IntelliJ - no Maven goals required. However, to give that client someone to chat to, you'll need to run two clients. To do this, run Maven's `package` goal, then browse to the `se325-example-13-client/target` folder. Then run the following command in a terminal:

```shell
java -jar se325-example-13-client-1.0-spring-boot.jar
```

You can run as many client instances as you like - just open a separate terminal for each one.


## The client
Within `ClientMain`, you can see how on line 38, we make an asynchronous web request to the service. We supply an instance of `InvocationCallback`, whose `completed()` method will be called by JAX-RS when the service responds - or whose `failed()` method will be called if there is a failure of some kind.

That callback can be seen on line 46. Whenever a message is received (in the `completed()` method on line 56), we display that message. In addition, we want to keep receiving chat messages after the server responds the first time. Therefore, on line 59, as soon as we receive a response from the server, we re-subscribe.


## The service
The JAX-RS resource class for the service - `ChatResource` - contains two web methods. The first (line 26) allows users to subscribe to be notified whenever any user sends a chat message. Their `AsyncResponse` object is stored for later use.

The second method (line 40) allows users to send messages. Upon receiving a message, the service simply sends that message to all subscribed clients, using the `AsyncResponse`'s `resume()` method. Once a sub's `resume()` method is called, it is removed from the list. This is because we can only send one response back to each sub. If that client wants to receive additional messages later, they need to resubscribe.

Note the use of `parallelStream()` on line 51. This is used so that we handle the `resume()` method of each `AsyncClient` in parallel. We do this so, if there's an error sending one client's response back (for example, if that client has already disconnected), then we won't break the functionality for the other remaining clients.

Then, note the use of `synchronized` on lines 30, 47, and 53. This is used to restrict access to the `subs` list to a single thread at a time, to prevent concurrent modification errors from occurring (it is an error to try and modify the contents of a `List` concurrently, with no thread safety protection. You are likely to get a `ConcurrentModificationException` in your code).
