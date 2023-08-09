# SOFTENG 325 Examples Repository

This repository contains example code demonstrating various concepts introduced in SOFTENG 325 at the University of
Auckland.

## Index

- *[Example 01](./example-01-tcp)*: Contains a simple example of how to send raw data over a TCP connection.

- *[Example 02](./example-02-java-serialization)*: Contains an example of how to serialize Java objects and send them
  across a network connection or convert them into a byte array.

- *[Example 03](./example-03-servlets)*: Contains an example of a simple Java Servlet.

- *[Example 04](./example-04-soap-jax-ws)*: Contains a simple "hello world" SOAP service written in Java.

- *[Example 05](./example-05-jax-rs)*: Contains two REST services written using JAX-RS.

- *[Example 06](./example-06-json-with-jackson)*: Contains several examples showing how to use Jackson to serialize /
  deserialize JSON from our Java programs.

- *[Example 07](./example-07-jaxrs-custom-serialization)*: Extends our parolee service running example with support for
  a custom data format - Java serialization.

- *[Example 08](./example-08-jaxrs-json)*: Extends our parolee service running example with support for JSON, without
  having to manually write out JSON strings.

- *[Example 09](./example-09-jpa-intro)*: A simple example showing JPA and Hibernate usage.

- *[Example 10](./example-10-auction-jpa)*: A much more complex JPA / Hibernate example, showing entity-entity
  relationships of various cardinalities, inheritance, and several examples of different kinds of JPQL queries.

- *[Example 11](./example-11-parolee-nojpa)*: A more thoroughly implemented Parolee web service. Written without JPA /
  Hibernate, for comparison with example 12 below.

- *[Example 12](./example-12-parolee-with-jpa)*: Identical to example 11, but uses JPA / Hibernate for persistence.

- *[Example 13](./example-13-chat-async)*: Shows how a rudimentary chat app can be build using JAX-RS support for
  asynchronous web services. Compare and contrast this with the WebSockets approach in Example 15.

- *[Example 14](./example-14-parolee-async)*: Builds on example 12, adding an asynchronous web method allowing users to
  subscribe to parole violations.

- *[Example 15](./example-15-chat-websockets)*: Shows how a rudimentary chat app can be build using WebSockets. Compare
  and contrast this with the async web services approach in Example 13.

- *[Example 16](./example-16-parolee-websockets)*: Shows how we can integrate WebSockets with a larger application, by
  extending our Parolee web service with support for subscribing to parolee movements.

### Bonus examples

- *[Bonus 01](./example-bonus-01-reflection)*: A "simple" program using Java Reflection to scan for, instantiate and
  invoke classes / methods with various annotations. Included to show some of the detail in how JAX-RS and other
  annotation-based libraries work behind the scenes.

- *[Bonus 02](./example-bonus-02-more-reflection)*: Another program showing off more aspects of reflection. In this
  example we can see how we can dynamically create proxy instances of interface types using Java's built-in Proxy
  class (part of its Reflection API). We can even create proxies of object types using the ByteBuddy package, which
  dynamically writes Java ByteCode for us. These techniques are used extensively in Hibernate for lazy loading and dirty
  checking purposes.