# SE325 Example 08 - Jackson integration with JAX-RS
This project shows how we can support JSON with our JAX-RS web services, using Jackson.

The web service itself is identical in functionality to the Parolee service introduced in [Example 05](../example-05-jax-rs). However, this time, it's set up to exchange data between client and service using JSON as the data format.

JAX-RS does not natively support this format. However, as JSON is such a commonly-used data format, adding this support is incredibly easy. All we need to do is include one extra dependency in our [POM file](./pom.xml) - on `resteasy-jackson2-provider`. We can see this dependency on line 48 of our POM. This dependency itself depends on the Jackson libraries (e.g. `jackson-core`, `jackson-databind`, `jackson-annotations`), so we don't need to manually add those dependencies ourselves - they'll be added automatically for us.

Now, whenever we mark one of end points as producing or consuming "application/json" (we can use the constant `MediaType.APPLICATION_JSON` to avoid hardcoding), Jackson will be used to serialize / deserialize outgoing / incoming HTTP responses / requests.

Unlike [Example 07](../example-07-jaxrs-custom-serialization), there is no need to include a custom `MessageBodyReader` / `MessageBodyWriter` implementation - these are provided by the extra dependency we added. Furthermore, JAX-RS can detect that the dependency contains these implementations, and will automatically configure our application with them without us having to manually reference them.

Similarly, when creating a new `Client` instance, we also do not need to configure it with references to any custom readers / writers. Compare the implementation of `ParoleeResourceIT`'s `createClient()` method, in this project and Example 07.
