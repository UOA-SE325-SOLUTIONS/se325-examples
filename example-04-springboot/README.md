# Example - Intro to SpringBoot

This project contains an example SpringBoot Web project demonstrating a simple REST service.

Important source files:

- `Example04SpringbootApplication`: This class allows the project to be run as a standalone Java application (which actually spins up a headless Tomcat server behind the scenes to self-host).

- `ServletInitializer`: This class allows the application to be deployed in a Servlet container.

- `GreetingController`: Contains our REST endpoint. It is configured with a `@RestController` annotation to mark it as a class offering RESTful methods, as well as a `@RequestMapping` to set the URL path that will be handled by this class.

  - Its `getHelloGreeting()` method handles `GET` requests to `/greetings/hello`, as configured by its `@GetMapping` annotation. It accepts a single `String` property, which is pulled from the provided `name` query parameter, as configured by the `@RequestParam` notation. It returns a `Greeting` object which will automatically be returned to the client as JSON (more on that in future examples).

Test files:

- `GreetingControllerUnitTest`: A standard JUnit tests of the `GreetingController` class.

- `GreetingControllerIT`: An _integration test_ of the greeting controller. This test, as configured by the `@SpringBootTest` and `@AutoConfigureMockMvc` annotations, will spin up a Spring application context for testing.

  - Its `@Test` methods use the `@Autowired` `MockMvc` object to send requests to the running Spring app, and examine their responses to determine correctness.

Configuration:

The `pom.xml` file was generated originally by IntelliJ's SpringBoot project helper, and contains many dependencies. I have also added the `maven-failsafe-plugin` to configure unit testing.

When Maven's `test` goal is run, it will ignore the integration tests because they don't end in `...Test.java`. I have configured the failsafe plugin to run all tests ending in `...IT.java` when the `failsafe:integration-test` or `verify` goals are run. This way, we can distinguish between unit tests and integration tests.