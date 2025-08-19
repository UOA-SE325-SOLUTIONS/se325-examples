# SOFTENG 325 Examples Repository

This repository contains example code demonstrating various concepts introduced in SOFTENG 325 at the University of
Auckland.

## Index

- **[Archive 2024 S2](./archive-2024-s2/)**: Contains old examples used as of _2024 Semester Two_. I have kept these here for your interest; however, many of these use old / outdated libraries that either have large updates or are not taught in the course anymore (e.g. JAX-RS is replaced with SpringBoot from 2025, and Hibernate / JPA annotations have been moved to later versions with `jakarta.persistence.*` packages rather than `javax.persistence.*`).

- **[Example 01](./example-01-tcp)**: Contains a simple example of how to send raw data over a TCP connection.

- **[Example 02](./example-02-java-serialization)**: Contains an example of how to serialize Java objects and send them across a network connection or convert them into a byte array.

- **[Example 03](./example-03-servlets)**: Contains examples of simple Java Servlets.

- **[Example 04](./example-04-springboot)**: Contains a simple SpringBoot Web application with a simple REST service, along with associated unit and integration tests.

- **[Example 05](./example-05-dad-jokes)**: Contains a more advanced REST service, allowing users to create and browse dad jokes.

- **[Example 06](./example-06-json-with-jackson)**: Contains standalone examples of how the Jackson library can be used to convert between Java objects and JSON.

- **[Example 07](./example-07-springboot-other-data-types)**: Shows how we can support data types other than JSON within our Spring apps.

- **[Example 08](./example-08-jpa-intro)**: An introduction to JPA / Hibernate, without SpringBoot.

- **[Example 09](./example-09-jpa-springboot)**: An introduction to Spring Data JPA, which integrates JPA / Hibernate into SpringBoot applications and provides convenience in the form of easier configuration and querying using Spring Data Repositories.

- **[Example 10](./example-10-parolee-springboot-jpa)**: Shows a more complex JPA / Hibernate domain model (for a Parolee service to track the whereabouts and criminal activities of parolees), and shows how Spring Web and Spring Data can be used easily together to create RESTful services backed by ORM & relational databases.

- **[Example 11](./example-11-auction-jpa)**: An auction (items / bids / users) JPA model. Shows of many more of the possible JPA annotations, including optional ones for changing column / table names, switching join columns to join tables, setting eager fetching / lazy loading, using Hibernate-specific fetch operations, custom query methods in Spring Data repositories, and more.

- **[Example 12](./example-12-chat-stomp-websockets)**: A simple chat webapp, with a backend powered by Spring / WebSockets / STOMP.

- **[Example 13](./example-13-parolee-websockets)**: Our Parolee web service from before, but with the added functionality that users can listen via WebSockets to be notified whenever a parolee's location changes. This project also shows off how we can test WebSocket code.
