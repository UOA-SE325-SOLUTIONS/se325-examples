# SE325 Example - Parolees web service with Spring Data JPA

This project shows a more complex domain model compared to previous examples, and shows how easily Spring Data JPA can be used in combination with Spring Web to create RESTful services backed by ORM & relational databases. The project also includes a set of integration tests to give you an idea about how such services can be tested.

Things to look for in this project:

1. You'll notice two parolee classes: `model.Parolee` and `dto.ParoleeDTO`. We have a simpler version of the parolee class the DTO - or Data Transfer Object) without the complex object graph which may be more difficult to send across as JSON. An alternative would be appropriate use of `@JsonIgnore` etc in the `Parolee` class itself.

2. The endpoint for `GET /parolees` has optional pagination (when the `page` query parameter is supplied). This uses Spring Data's built-in pagination support, with the `PageRequest` and `Page` classes. With this, we get pagination essentially "for free" when using Spring Data repositories.

3. In the integration tests, we have a `@BeforeEach` setup method which will clear and repopulate the database before each test. In this project, since all application state is stored in the database, this means we don't need to use `@DirtiesContenxt` to recreate the entire Spring context for every test. Therefore, the tests run a fair bit faster for this project compared to other, seemingly simpler, services earlier in the course.