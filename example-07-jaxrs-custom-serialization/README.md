# SE325 Example 07 - Custom serialization with JAX-RS
This project shows how we can support arbitrary data formats with our JAX-RS web services.

The web service itself is identical in functionality to the Parolee service introduced in [Example 05](../example-05-jax-rs). However, this time, it's set up to exchange data between client and service using *java serialization* as the data format.

JAX-RS does not natively support this format, but can easily be extended to support arbitrary formats. We have created a class, [`SerializationMessageBodyReaderAndWriter`](./src/main/java/se325/example07/parolee/services/SerializationMessageBodyReaderAndWriter.java), which implements the `MessageBodyReader` and `MessageBodyWriter` interfaces. We have also annotated the class with both a `@Produces` and `@Consumes` annotation for the "application/java-serialization" MIME type (defined by the `APPLICATION_JAVA_SERIALIZED_OBJECT` constant). We have then told JAX-RS about our new reader / writer class by adding it to the `ParoleeApplication`'s `classes` list (lines 21, 25, 36).

Now, when we mark a method in our `ParoleeResource` class as *consuming* application/java-serialization, the incoming HTTP request body will be run through our reader's `readFrom()` method, and supplied directly as an argument to the endpoint method (e.g. `ParoleeResource`'s `createParolee()` method). Similarly, when we mark a method as *producing* application/java-serialization, the method's return value will be serialized via our writer's `writeTo()` method before being sent back to the client in the HTTP response body.

## Client
If we want to build an HTTP client which supports our custom data format, JAX-RS also provides a way for us to do this. The `ClientBuilder` class, which allows us to create new HTTP `Client` instances, contains a `register()` method. This method allows us to reference any custom readers and writers we like. We can see an example of this in [`ParoleeResourceIT`](./src/test/java/se325/example07/parolee/test/ParoleeResourceIT.java)'s `createClient()` method (line 52).
