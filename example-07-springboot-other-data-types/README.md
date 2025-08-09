# SE325 Example - Supporting other data types in Springboot

Springboot supports JSON by default, using Jackson. We can support other data types easily.

To use another data type with built-in support, we simply add `produces` and / or `consumes` properties to our `Mapping` annotations. For example, in this project, `GreetingController` has two methods which return "Hello World" greetings:

- One of them is configured to produce `application/json` content, which will result in the returned `Greeting` object being converted to JSON using Jackson.

- The other is configured to produce `text/plain`. We are just returning a `String` from this method, which will be returned directly to the client.

If we want to extend Spring, we can add `AbstractHttpMessageConverter` implementations to convert between arbitrary MIME types (including our own custom ones) and Java classes.

In this project, we have implemented `BufferedImageHttpMessageConverter` to convert between `BufferedImage`s and `image/png` binary data. We have registered it with our Spring app using the `@Configuration` class, `WebConfig`, which will be auto-loaded on startup. Finally, in `ImageController`, we can see that as long as we set the `produces` or `consumes` properties correctly, then Spring will handle the conversion for us, now that we've registered our custom converter class.
