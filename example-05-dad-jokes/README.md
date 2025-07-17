# Example - SpringBoot Dad Jokes Service

This project contains a slightly more advanced web service compared to Example 4.

Important things to note:

- In `DadJokesController`, we can see:

  - Use of an optional query parameter, in the `getAllDadJokes()` method.
  
  - An example of handling POST requests, with `@PostMapping` and `@RequestBody`, in the `addDadJoke()` method.

  - An example of reading path parameters, with `@PathVariable`, in the `getDadJokeById()` method.

- In `DadJokesControllerIT`, we can see:

  - Use of the `@DirtiesContext` annotation to make sure that each test case runs in its own separate SpringBoot context. This prevents any modifications (such as adding dad jokes) from "leaking" into other test cases. Since a brand new Spring context needs to be created in each case, this does result in a performance hit when running the tests.

  - More complex use of `jsonPath()` to do things like check whether something is an array, check its length, and check values inside the array.

  - Detecting HTTP status codes other than OK (400 Bad Request and 404 Not Found, in this case)
