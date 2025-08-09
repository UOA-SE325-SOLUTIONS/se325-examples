# SE325 Example - JPA / Hibernate with SpringBoot / Spring Data JPA dependency

In this example, we show how we can use the Spring Data JPA addon for Spring, to bring JPA / Hibernate support to our SpringBoot applications. This example is a command-line application with no web / REST service support, just showing the JPA / Hibernate integration itself.

## Application configuration

Spring Data abstracts away the need for defining a persistence unit in `persistence.xml` by providing sensible defaults. All entity classes included in the same package as the `@SpringBootApplication` being run (and child packages) will be included in the model.

Defaults can be overridden in Spring's `application.properties` file. For example, in this project, we are setting up the database connection which will be used (an in-memory H2 database instance).

## Spring Data repositories

We are using the same `Message` / `Comment` persistent classes as the previous example. However, this time, we are accessing them through a Spring Data Repository ([`MessageRepository`](./src/main/java/uoa/se325/example09jpaspringboot/repository/MessageRepository.java)). This interface extends Spring's `Repository` interface, and we are supplying the entity type (`Message`) and its primary key type (`Long`) as generic type arguments. The interface already includes methods for standard CRUD operations, and we are adding two more - for finding messages with particular content, and for finding messages with a creation time in a given range.

Note that we don't actually have to write an implementation of this interface! Using a combination of two powerful Java language features ([reflection](https://www.baeldung.com/java-reflection) and [dynamic proxies](https://www.baeldung.com/java-dynamic-proxies)), Spring is reading our interface and the names of its methods, and creating its own implementation of our interface at runtime which functions as we expect!

The repository implementation uses Hibernate's `EntityManager` behind the scenes, but we don't have to manually interact with it unless we want to (we can gain access to it using an `@Autowired` property).

To implement the functionality of the defined interface, Spring will read the names of that interface's method definitions as [_JPA Query Methods_](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html), and implement their functionality according to the rules defined in the linked article. 

## Transaction support

In addition to abstracting away the `EntityManager`, Spring Data repositories also attempt to simplify transaction management.

By default, each repository method is its own transaction, and its own Hibernate session. This works well in many cases, but you can run into issues if you want to define a larger transactional boundary consisting of multiple repository operations, or if you are using lazily loaded collection types (these won't be able to be accessed outside the session).

By marking your method using repository operations as `@Transactional`, Spring will instead use one single transaction and Hibernate session for that entire method. If you want to define smaller transactions, then you will need to refactor your code into multiple methods, and use `@Transactional` with each of them.
