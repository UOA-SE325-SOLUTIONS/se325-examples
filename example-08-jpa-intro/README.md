# SE325 Example - Intro to JPA / Hibernate
This project contains a simple example to introduce some concepts regarding JPA (Java Persistence Annotations), and the implementation we're using - Hibernate.

## Annotations
In the [`helloworld`](src/main/java/se325/example08/helloworld) package, the `Message` class is annotated as an `@Entity`. Entities are objects that have their own lifecycles, and their own database tables. Entity types need a unique identifier. In this case, the `id` property is annotated with `@Id` to signify this as being the primary key. It is also annotated with `@GeneratedValue`, to specify that the id should be assigned by Hibernate when adding it to the database.

`Message`s can have any number of `Comment`s. Comments are defined as `@Embeddable`, meaning that they are *value types*, rather than *entity types*. These objects don't have their own lifecycle, and are tied to their containing entity. The collection of comments in a message (`comments`) has been annotated with `@ElementCollection` to signal to JPA that this is a collection of value types.

## Entity managers and Entity Manager Factories
In `HelloJPAMain`, on line 20, we're creating an `EntityManagerFactory` using the configuration `se325.example08.helloworld`. This refers to the `<persistence-unit name="se325.example08.helloworld">`, located in [`resources/META-INF/persistence.xml`](./src/main/resources/META-INF/persistence.xml). In that file, we can see a `<class>` element defined, pointing to our `Message` class. We should reference all our entity types this way - or, we can use `<exclude-unlisted-classes>false</exclude-unlisted-classes>` to automatically include all defined entity types.

For the remainder of `HelloJPAMain`, we're creating an `EntityManager` and using it to run several database operations. Firstly, we're creating a new `Message` with some `Comment`s, and persisting it to the database. Then, we're querying for a list of messages already in the database. Then, we're modifying one of the messages. For each operation, we surround it in `entityManager.getTransaction().begin()` and `entityManager.getTransaction().commit()`. More on transactions in a later example!

## The H2 database
In this project's [POM](./pom.xml), we're adding an embedded H2 database implementation. H2 is a lightweight relational database that is commonly used for testing purposes. In our `persistence.xml`, lines 10 thru 13 define the connection information which Hibernate uses to connect to our H2 instance (in this case, an in-memory database). We could replace this with connection info for any other relational database, should we choose.

On line 15, we're configuring Hibernate to drop all database tables when a new entity manager factory is created. This is a very useful setting for testing purpose, but we would obviously change it for production.
