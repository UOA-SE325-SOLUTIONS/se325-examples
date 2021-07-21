# SE325 Example 04 - SOAP with JAX-WS
This project contains a simple "Hello World" SOAP service, and corresponding client, written using JAX-WS.


## Service
To build SOAP services with JAX-WS, we first create a Java interface describing the service. In our case, this is `se325.example04.server.HelloWorld`. It contains a single method that takes a String argument and returns a String. The service and method are annotated with appropriate SOAP annotations, which inform the runtime that this interface describes an RPC SOAP service, and that `getHelloWorldAsString()` is a web service method.

We then need to implement that interface to provide our service logic - `HelloWorldImpl` in our case. We also need to tag the implementing class with a `@WebService` annotation, pointing to the interface which describes the service (i.e. `HelloWorld` in our case).

Finally, we can publish the service and make it available to clients, using the `Endpoint.publish()` static method, as demonstrated in `HelloWorldPublisher`. Here, we describe the host, port, and URL path onto which the service should be published.

Once we run `HelloWorldPublisher`, we can navigate to <http://localhost:10000/ws/hello> in the browser. This will show an HTML file describing the service. The page will also include a link to the service's WSDL: <http://localhost:10000/ws/hello?wsdl>.

Also important to note on the HTML page displayed in the browser is the service name. You'll see a string like this:

```text
Service Name:	{http://server.example04.se325/}HelloWorldImplService
```

We need the service URI (`http://server.example04.se325/`) and service name (`HelloWorldImplService`), in addition to the URL above, in order to consume the service from our client.

This info is also available to view in the WSDL, in the `targetNamespace` and `name` attributes of the root `<definitions>` element, respectively. The WSDL also contains a generated description of the service methods available, and their request and response datatypes.


## Client
As mentioned above, we need three pieces of information in order to consume our SOAP service: the service URI and name (from the WSDL), and the URL where the WSDL is published.

On line `16` of `HelloWorldClient`, the call to `Service.create()` will search the WSDL at the given URL, for the service with the given URI and name (in the supplied `QName` instance). If found, we can connect to and consume the service. If not, an exception will be thrown.

Then, on line 17, we actually connect to the service, and obtain a reference to it, typed as a `HelloWorld` implementation. In this way, developers are shielded from having to write the XML inputs and outputs of the service themselves - they can simply use the service as if it were a remote Java object, and let the JAX-WS middleware take care of marshalling / unmarshalling in XML format.
