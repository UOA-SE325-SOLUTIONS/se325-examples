# SE325 Example - TCP
This project contains a simple example of how to setup a simple TCP client and server using Java.

The [`Client`](./src/main/java/se325/example01/basictcp/Client.java) class demonstrates how to use a `Socket` to establish a TCP connection to a server. Once established, communication via TCP sockets is **full-duplex** (i.e. *two-way*). This is done via `InputStream` and `OutputStream` subclasses.

The [`Server`](./src/main/java/se325/example01/basictcp/Server.java) class demonstrates how to use a `ServerSocket` to listen for client connections. Once established, a `Socket` instance will be obtained which can be used to communicate with an individual client. The `ServerSocket` may continue listening for additional clients on another thread.
