# SE325 Example 05 - REST services with JAX-RS
This project contains two RESTful web services, created using the JAX-RS library.

## Hello World service
This extremely simple service is defined in the [`helloworld.server`](./src/main/java/se325/example05/helloworld/server) package. It consists of two classes:

### `HelloApplication`
This is the main configuration class for the service. Points to other classes which together define the service. In this case, `GreetingsResource`. The `@ApplicationPath()` annotation at the top of the class defines the path of this particular service, within the hosting servlet, to be `/services`. So, if the server is running on `localhost:8080`, and the application context path is `/example_05_jax_rs_war`, then the service defined by this class would handle requests to `http://localhost:8080/example_05_jax_rs_war/services/*`.

### `GreetingsResource`
This defines some endpoints for the service. In this case, a single endpoint, defined by the `sayHello()` method.

The method takes a single argument - `name`, which will be populated by the `name` *query parameter*. The query string of the request URL is everything after the `?`. Query parameters are defined by key-value pairs, `key=value`, separated by an `&` if there are more than one. For example, the URL `http://myserver/myservice?name=Andrew&age=21` would have two query parameters:

- name = Andrew
- age = 21

For this method, the `name` argument is set to have a default value of "Human", if there is no `name` query parameter.

The method creates a JSON string and sends it back to the client, with the standard "ok" (200) status code. The `@Produces` annotation lets the client know that this method produces JSON content. The `@GET` annotation specifies that this method handles `HTTP GET` requests. The `@Path` annotations on the method, and again on the class itself, join with the `@ApplicationPath` annotation already defined in `HelloApplication`. So, in this case, continuing the example from above, if a client wanted to invoke this particular method, they would make a `GET` request to <http://localhost:8080/example_05_jax_rs_war/services/greetings/hello>.

### Running the service
A simple Java client for this service is defined within the `helloworld.client` package. In addition, [`index.html`](./src/main/webapp/index.html) demonstrates JavaScript code which can be used to invoke the service. Finally, we could navigate directly to the URL above in our browser (assuming the host, port, and application context are the same) to see the JSON response rendered directly in the browser.


## Parolee service
This service, defined in the [`parolee`](./src/main/java/se325/example05/parolee) package, forms the beginnings of our running-example Parolee web service. The `ParoleeResource` class defines several endpoints, conforming to the REST standar, for creating, retrieving, updating, and deleting parolees. In addition, there is code to convert JSON strings to and from `Parolee` instances. This code looks a little ugly, but don't worry - we can do this in a much nicer fashion as you'll see in upcoming examples!

Also in this class you'll see examples of annotations used for other HTTP methods (`@POST`, `@PUT`, `@DELETE`), along with the `@PathParam` annotation. This defines a path parameter, which is a section of the URL path that you can read. One example in this file is `@Path("{id}")`, which will match any value and assign it to the `id` path parameter. This path param is then assigned to one of the method arguments using `@PathParam("id")`. The result is that for URL paths `.../parolees/2` and `.../parolees/3`, the value of `id` will be `2` and `3`, respectively.

Finally, you'll also see the `@Consumes` annotation, here being used to let JAX-RS know that it can accept JSON data being supplied to the annotated endpoints.

### Testing the service
There is a JUnit integration test for the parolee service, in the [`ParoleeResourceIT`](./src/test/java/se325/example05/parolee/ParoleeResourceIT.java) class. The test cases in this file assume that this project is hosted on `http://localhost:10000/`, with the application context set to `/`. Luckily, we don't even have to configure Tomcat and manually start it running before running our test!

If we run Maven's `verify` goal, integration tests will be run. In [`pom.xml`](./pom.xml), line 132, we have configured a Jetty server to spin up prior to our integration tests running, with our project hosted on it.

Line 97 configures standard unit testing, which happens during the `test` goal (which is *before* `verify`), to ignore our integration tests (marked with an `IT` suffix). And, line 112 configures the integration tests to run in the `verify` goal.

The end result is that if you run the `verify` goal, you'll notice a Jetty server spin up, then our integration tests will be run. Finally, the Jetty server will automatically be stopped before Maven completes.