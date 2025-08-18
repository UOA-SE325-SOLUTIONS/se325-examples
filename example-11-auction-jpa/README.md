# SE325 Example - Auction JPA / Hibernate example

This project shows off an auction system, with a non-trivial class hierarchy. It uses several JPA annotations to instruct Hibernate on how to properly persist its state.

Some things you can see in this example project:

## Domain model configuration & customization

- **Entity table customization**: In the `User` class, we can see how to change an entity table's name using `@Table(name = "USERS")`.

- **Column customization**: Many table columns are cusomized in this project, in many classes. For example, in the `Address` class, we are defining three columns with custom names a maximum text lengths, and for two of them we are defining them as "non-nullable" (meaning that values are required - we will get an exception if we try to save an `Address` with a `null` `city`, for example).

- **Customization of value-type columns**: In `User`, we have several embedded `Address`es (billing, home, etc). By default, each of these would have the same column names in the user table, causing a naming conflict. Using `@AttributeOverrides`, we are able to override the column names specified in the `Address` class, to ensure that each one of a user's addresses is placed in different columns.

- **Fetch strategies**: Many of the associations (`@ManyToOne`, `@ManyToMany`, `@ElementCollection`, etc) are marked with `fetchType` properties specifying eager fetching or lazy loading. Using these, we can override the default behaviour. By default, `@Embedded` and `@ManyToOne` relationships are eager-fetched, while the others are lazy-loaded.

- **Hibernate select modes**: In _Hibernate_ specifically, we can use the `@Fetch` annotation (from the `org.hibernate.annotations` package) to change how data is fetched for certain collections. These can optimize the number and type of database queries which are performed. Examples and explanations are provided in the `Item` class.

- **Complete customization of entity relationships**: Using `@JoinColumn` and `@JoinTable`, we can completely customize how various relationships are represented in the database. At minimum, we can use `@JoinColumn` to customize the name of a join column and mark it as non-nullable, as seen in `Bid`'s `item` property. Going further, we can completely change the underlying database structure. For example, `Item` has a many-to-one relationship with `User` (one user can buy many items; each item can only be bought by one user). This could normally be represented by a join column on the items table (containing the id of the user which bought the item, or `null` if not bought yet), but in the `Item` class we can see how we can use `@JoinTable` to instead use a separate table for mapping users to the items they purchase.

- **Entity versioning**: By giving an entity class a `@Version` property, such as `Item`'s `version`, we can enable the use of Optimistic Concurrency Control with this entity type.

## Spring Data repository queries

- **Query methods**: We can see several instances of query methods, created by following the [JPA query method naming conventions](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html). For example, `BidRepository` contains a method to get the most recent bid for a given item. By appropriately naming the method, we can avoid writing any JPQL or Java code in many cases.

- **Custom query methods**: When the naming syntax above is not sufficient, we can annotate our repository methods with JPQL code to execute, using the `@Query` annotation in combination with `@Param` annotations marking the method parameters. For example, in `ItemRepository`, the `findByIdEagerFetchBids()` method will execute JPQL to load an item and eager-fetch the item's bids (the default is lazy loading).

- **Concurrency control**: By using the `@Lock` annotation on a repository method, such as `ItemRepository`'s `findByIdForUpdate()` method, we can specify one of the currency control modes to use for that query (`OPTIMISTIC`, `OPTIMISTIC_FORCE_INCREMENT`, `PESSIMISTIC_READ`, `PESSIMISTIC_WRITE`). When using the optimistic modes, make sure the entity type has a `@Version`.

## Use of transactions

The `BiddingService` class contains logic to allow users to place bids on items. We want to enforce that the most recent bid on an item _must_ also be the highest-value bid on that item, and that a newly-placed bid _must_ be "more recent" and "higher value" than the most recent prior bid. Without concurrency control, it would be very easy for multiple concurrent bidding operations to break this invariant (both lost updates and stale reads are possible in this scenario).

Using OCC with forced version incrementing, or pessimistic write locks, we can provide a solution to this. The logic is explained in the `BiddingService` comments, as well as `ItemRepository`.
