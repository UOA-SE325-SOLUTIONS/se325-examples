# SE325 Example 15 - Chat app using WebSockets
This example shows how a rudimentary chat app can be build using Java's JSR 356 WebSockets API. Compare and contrast this with the WebSockets approach in [Example 13](../example-13-chat-async).


## Running the app
The server can be run within Tomcat, due to its native JSR 356 support. Follow the instructional video on Canvas if you need a reminder of how to do this. The client code assumes an app context of `/`, but this can be changed by editing `ClientMain` line 15 if desired.

Once the server is running, one instance of the client can be run by running `ClientMain` directly from IntelliJ - no Maven goals required. However, to give that client someone to chat to, you'll need to run two clients. To do this, run Maven's `package` goal, then browse to the `se325-example-15-client/target` folder. Then run the following command in a terminal:

```shell
java -jar se325-example-15-client-1.0-spring-boot.jar
```

You can run as many client instances as you like - just open a separate terminal for each one.


## The common library
Compared with Example 13, this example's common library is similar - it contains a `Message` class, which is a POJO to be sent between chat participants, as JSON. However, you'll notice on line 20 of the `Message` class, another class called `Coder`. This class integrates Jackson / JSON and the WebSockets implementation. Unlike JAX-RS for REST services, JSR 356 does not provide for seamless support of Jackson and other JSON libraries - but nevertheless, it is easy to set up, as we will see.

`Message.Coder` is an instance of `JSONCoder`, which can be seen in the `se325.websocketchat.jackson.websocket` package. This class, adapted from one found at <https://dzone.com/articles/using-java-websockets-jsr-356>, implements the WebSocket API's `Encoder.TextStream` and `Decoder.TextStream` interfaces, and uses Jackson's `ObjectMapper` to map between Strings and POJOs.


## The Java client
In `ClientMain`, we have configured the class with the `@ClientEndpoint` annotation. This marks instances of this class as being able to act as WebSocket endpoints. We have also configured the API to use our `Message.Coder` class to encode and decode messages. This way, we can directly send and receive `Message` instances - the WebSocket implementation will automatically invoke the encoders / decoders when required.

The `start()` method shows how we can establish a WebSocket container, and connect to a WebSocket server at a particular URI (notice the `ws` rather than `http` protocol in the URI). We provide an instance, or class, of an endpoint class (`this`, in this case), and we receive a `Session` object representing the WebSocket connection.

The `onMessage()` method (line 50) is configured with the `@OnMessage` annotation. This method will be called by the WebSocket API whenever it receives a message. The message is automatically converted to a `Message` instance using the configured decoder.

Inside the `chatLoop()` method, on line 39, we are sending a `Message` instance to the chat server. The message is automatically converted to a JSON string using the configured encoder.

As JSR 356 is an API, not an implementation, we need to provide an implementation. For the client, we're using the Tyrus standalone client, which is a reference implementation, It's added as a dependency in the client POM file, line 56.


## The HTML / JavaScript client
A simple JavaScript WebSocket client to the chat app is also given for your interest, in `se325-example-15-service/src/main/webapp`. `index.html` provides the UI, while `js/main.js` provides the code. One of the strengths of WebSockets is their seamless support in all modern browsers and other JavaScript engines.

On line 17, we connect to the server, creating a `WebSocket` instance. Then on line 18, we set our `onReceiveMessage` function as the callback to invoke when a message is received from the server.

On line 50, we use `JSON.stringify()` to convert a JS object into a JSON string, and send that as a message to the chat service. On line 56, we parse a received JSON sring as a JS object using `JSON.parse()`, and display it on our webpage.

More recently, the [Socket.io](https://socket.io/) library has gained huge traction, becoming the de facto standard for JavaScript-based WebSocket clients and servers (e.g. node.js). If you're interested in WebSockets in JavaScript, I would encourage you to have a look at Socket.io, which provides several extensions over basic WebSockets.


## The service
The server endpoint class - `ChatServiceEndpoint` - is configured with the `@ServerEndpoint` annotation. Like `@ClientEndpoint`, we specify the encoders and decoders to use. In addition, we specify the path of this endpoint on the server. In that path, we can specify path parameters, which we have done here (`{username}`).

Whenever a new client connects to our servlet container using the path configured above, a new instance of our `ChatServiceEndpoint` class will be created. That is, there will be one active instance of `ChatServiceEndpoint` for each active WebSocket client. Once an instance is created, the method (if any) marked with the `@OnOpen` annotation will be called - `onOpen()` (line 52) in this case. In this method, we receive an instance of `Session` which can be used to communicate with that client. We can also retrieve the values of any path parameters at this point - as we are doing with the `username` param - using the `@PathParam` annotation. In this method, we're simply storing a reference to the newly created endpoint in a global (static) list - so we can easily send messages to all clients later.

Whenever any client sends a message, we'll receive it in the method annotated with `@OnMessage` (line 68). Again, the `Message` instance is automatically decoded from a String using the configured decoder. This simple chat server just broadcasts any received messages to all clients, in the `broadcast()` method on line 105.

When a client disconnects, our `@OnClose`-annotated method will be called (`onClose()`, line 78). Here, we remove the disconnected endpoint from the list of all endpoints, so we stop trying to send more messages to it later.
