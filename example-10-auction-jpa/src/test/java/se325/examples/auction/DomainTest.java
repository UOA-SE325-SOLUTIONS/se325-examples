package se325.examples.auction;

import org.hibernate.LazyInitializationException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * Test class to illustrate the behaviour of JPA in generating relational
 * schemas and SQL, and with more generally managing object persistence.
 * <p>
 * This class inherits from JpaTest, which manages database connectivity,
 * JPA initialisation and takes care of clearing out the database immediately
 * prior to executing each unit test. This ensures that there are no side-
 * effects of running any tests.
 * <p>
 * To see the effect of any particular test, you may want to comment out the
 *
 * @author Ian Warren
 * @Test annotations on other tests. You can then use the H2 console to view
 * the effect of the test of interest.
 */
public class DomainTest extends JpaTest {

    private static Logger _logger = LoggerFactory.getLogger(DomainTest.class);

    /**
     * Persists Item entities containing Image ValueTypes. This test then runs
     * a query to check that a persisted object can be retrieved as expected.
     */
    @Test
    public void persistItems() {
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndImages();
        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Run a query to obtain the database identity for the DVD Ttem.
        TypedQuery<Long> queryForId = entityManager
                .createQuery("select item.id from Item item where item.name like '%DVD%'", Long.class);
        Long dvdId = queryForId.getSingleResult();

        // Run a query load the DVD Item (this could have been done in the
        // above query by returning the full Item ather than only its database
        // identity.
        TypedQuery<Item> query = entityManager
                .createQuery("select item from Item item where item.id = :id", Item.class)
                .setParameter("id", dvdId);
        Item dvd = query.getSingleResult();

        // The Item should have three images.
        assertEquals(3, dvd.getImages().size());

        // Check that the Item's Image Set includes an expected Image. The
        // Set will perform a value-based check to compare the given Image with
        // the Set's members.
        assertTrue(dvd.getImages().contains(new Image("DVD booklet", "image_678.png", 1024, 768)));
    }

    /**
     * Persists Item entities that are associated with Bid entities. This test
     * then runs some queries to check that the persisted objects can be
     * retrieved from the database as expected.
     */
    @Test
    public void persistItemWithBids() {
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        // Cause the Items and Bids to be persisted. Because Item specifies
        // transient (cascading) persistence for its associated Bids, the Bids
        // will be persisted automatically when the Items are persisted.
        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Query the iPhone Item and check that its associated Bid was persisted.
        TypedQuery<Item> queryItem = entityManager
                .createQuery("select item from Item item where item.name like :name", Item.class)
                .setParameter("name", "%Phone");
        Item queryResultItem = queryItem.getSingleResult();

        TypedQuery<Bid> queryBid = entityManager
                .createQuery("select bid from Bid bid where bid.item = :item", Bid.class)
                .setParameter("item", queryResultItem);
        Bid queryResultBid = queryBid.getSingleResult();

        assertEquals(queryResultBid, queryResultItem.getBids().iterator().next());

        // Query the DVD item and check that its associated 3 Bids were
        // persisted.
        queryItem.setParameter("name", "%DVD");
        queryResultItem = queryItem.getSingleResult();

        // Lookup the Bids for the DVD Item. There should be 3 Bids.
        TypedQuery<Bid> queryBids = entityManager
                .createQuery("select bid from Bid bid where bid.item = :item", Bid.class)
                .setParameter("item", queryResultItem);
        List<Bid> queryResultBids = queryBids.getResultList();
        assertEquals(2, queryResultBids.size());
    }

    /**
     * Persists a number of Item entities and then deletes one of them. This
     * test checks that the deleted Item has actually been removed from the
     * database.
     */
    @Test
    public void deleteItemWithBid() {
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Start a new transaction to run a query for the iPhone Item.
        entityManager.getTransaction().begin();

        TypedQuery<Item> queryItem = entityManager
                .createQuery("select item from Item item where item.name = :name", Item.class)
                .setParameter("name", "iPhone");
        Item fetchedItem = queryItem.getSingleResult();

        // Get the iPhone Item's Bid.
        Bid fetchedBid = fetchedItem.getBids().iterator().next();
        Long fetchedBidId = fetchedBid.getId();

        // Delete the Bid and Item. Note that the two remove() calls are
        // necessary and in this order. Cascading removal is not configured by
        // Item's many-to-one association with Bid - so the associated Bid must
        // be explicitly removed. The BIDS table has a foreign key constraint
        // on ITEMS, hence the BIDS row must be removed first - otherwise a
        // database integrity constraint will be violated, leading an exception
        // on commit().
        entityManager.remove(fetchedBid);
        entityManager.remove(fetchedItem);
        entityManager.getTransaction().commit();

        // Rerun the query to attempt to load the iPhone Item. Check that it
        // doesn't return the Item.
        try {
            queryItem.getSingleResult();
            fail();
        } catch (NoResultException e) {
            // No action necessary - this exception is expected.
        }

        // Check that the Bid is also no longer in the database.
        TypedQuery<Bid> queryBid = entityManager
                .createQuery("select bid from Bid bid where bid.id = :id", Bid.class)
                .setParameter("id", fetchedBidId);
        try {
            queryBid.getSingleResult();
            fail();
        } catch (NoResultException e) {
            // No action necessary - this exception is expected.
        }
    }

    /**
     * Persists a number of CreditCard and BankAccount entities. Once persisted
     * this test runs a polymorphic query to load the BillingDetails objects.
     */
    @Test
    public void queryBillingDetails() {
        entityManager.getTransaction().begin();

        BillingDetails[] accounts = new BillingDetails[5];
        accounts[0] = new CreditCard("Amy", "4999...", "Apr-2015");
        accounts[1] = new CreditCard("Kim", "4999...", "Mar-2017");
        accounts[2] = new BankAccount("Pete", "50887471", "ANZ");
        accounts[3] = new BankAccount("John", "83846883", "ASB");
        accounts[4] = new CreditCard("Geoff", "4556...", "Dec-2016");

        for (int i = 0; i < accounts.length; i++) {
            entityManager.persist(accounts[i]);
        }

        // Cause the BillingDetails objects to be stored in the database.
        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Run a query to load a fresh set of BillingDetails objects.
        List<BillingDetails> billingDetails = entityManager.createQuery("select bd from BillingDetails bd", BillingDetails.class).getResultList();
        assertEquals(5, billingDetails.size());

        // Check that each persisted BillingDetails object is in the query's result.
        for (int i = 0; i < accounts.length; i++) {
            billingDetails.remove(accounts[i]);
        }
        assertTrue(billingDetails.isEmpty());
    }

    /**
     * Persists a few User entities with associated BillingDetails. This test
     * then runs a query to retrieve the persisted Users and to check that the
     * loaded objects have the expected associations with BillingDetails
     * objects.
     */
    @Test
    public void persistUsersWithBillingDetailsAndRunQuery() {
        // Populate database.
        entityManager.getTransaction().begin();
        populateDatabaseWithUsersAndBillingDetails();
        entityManager.getTransaction().commit();

        // Clear the persistence context, removing all managed objects.
        entityManager.clear();

        // Run a query to retrieve all persisted Users.
        List<User> users = entityManager.createQuery("select user from User user", User.class).getResultList();
        assertEquals(3, users.size());

        for (User user : users) {
            String username = user.getUserName();
            if (username.equals("amy") || username.equalsIgnoreCase("neil")) {
                // Users Amy and Neil share a CreditCard object.
                BillingDetails bd = user.getDefaultBillingDetails();
                assertEquals("Amy", bd.getOwner());
            } else {
                // User Felix isn't associated with a BillingDetails object.
                assertNull(user.getDefaultBillingDetails());
            }
        }
    }

    /**
     * Persists a few User entities with associated BillingDetails. This test
     * then runs a query to retrieve one of the User objects. The test shows
     * that the User's BillingDetails object is lazily loaded (i.e. loaded
     * on demand when accessed).
     */
    @Test
    public void lazilyLoadAUsersBillingDetails() {
        // Populate database.
        entityManager.getTransaction().begin();
        populateDatabaseWithUsersAndBillingDetails();
        entityManager.getTransaction().commit();

        // Clear the persistence context, removing all managed objects.
        entityManager.clear();

        // Run a query to load a User object representing Amy.
        TypedQuery<User> query = entityManager
                .createQuery(
                        "select u from User u " +
                                "where u.username = :name", User.class)
                .setParameter("name", "amy");
        User user = query.getSingleResult();

        // The class of object returned by User's getBillingDetails() method
        // is NOT CreditCard as you'd normally expect. This is because the
        // many-to-one association on BillingDetails, defined in class User,
        // lazily loads the BillingDetails instance. Hence, when a User object
        // is loaded from the database, it's associated BillingDetails object
        // isn't loaded until it's accessed. When the User is loaded, the JPA
        // provider doesn't know what type of BillingDetails object (i.e.
        // BankAccount or CreditCard) is associated with the User. A separate
        // query to the database is required to determine this - and we try to
        // avoid making another database hit with lazy loading. So, the JPA
        // provider returns a "proxy" / placeholder class for the
        // BillingDetails object. The proxy implements all BillingDetails
        // methods but not any subclass-specific methods.
        BillingDetails bd = user.getDefaultBillingDetails();
        assertThat(bd.getClass().getName(), not(equalTo(BillingDetails.class.getName())));
        assertFalse(bd instanceof CreditCard);
        assertTrue(bd instanceof BillingDetails);

        // Invoke a BillingDetails method on the retrieved BillingDetails
        // object. Invoking the proxy object causes it to load the CreditCard
        // object the proxy represents.
        bd.pay(new BigDecimal(80.00));
    }

    /**
     * Persists a few User entities with associated BillingDetails. This test
     * then runs a query to retrieve one of the User objects. This test is
     * similar to lazilyLoadAUsersBillingDetails(), but illustrates a different
     * way to access the lazily loaded User's BillingDetails object. Test
     * lazilyLoadAUsersBillingDetails() doesn't allow the BillingDetails object
     * to be manipulated as a CreditCard instance (note that the return type of
     * User's getDefaultBillingDetails() method is BillingDetails). The loaded
     * User's BillingDetails object is actually a CreditCard instance. This
     * test shows how it can be lazily loaded and operated on as such, so that
     * CreditCard-specific methods can be invoked.
     */
    @Test
    public void lazilyLoadAUsersCreditCard() {
        // Populate database.
        entityManager.getTransaction().begin();
        populateDatabaseWithUsersAndBillingDetails();
        entityManager.getTransaction().commit();

        // Clear the persistence context, removing all managed objects.
        entityManager.clear();

        // Run a native SQL query to find the database identity of the
        // BillingDetails object owned by User Amy. The SQL query returns a
        // BigInteger result that needs to be converted to a Long.
        Query query = entityManager.createNativeQuery(
                "select bd.ID from BILLINGDETAILS bd " +
                        "where bd.OWNER = :ownerName")
                .setParameter("ownerName", "Amy");
        BigInteger result = (BigInteger) query.getSingleResult();
        Long id = result.longValue();

        // To retrieve the user's BillingDetails object in such a way that it
        // can be operated on as a CreditCard object, a different technique
        // must be used. EntityManager provides a method named getReference()
        // that returns a proxy of a given type. getReference() doesn't load
        // the actual object represented by the proxy from the database; the
        // proxy contains only the ID of the represented object. In this case,
        // the proxy is a subclass of CreditCard and therefore inherits
        // CreditCards' methods. When invoked, the proxy causes the
        // (CreditCard) object it represents to be loaded and, in turn,
        // invoked via the proxy.
        CreditCard creditCard = entityManager.getReference(CreditCard.class, id);
        assertThat(creditCard.getClass().getName(), not(equalTo(CreditCard.class.getName())));
        assertTrue(creditCard instanceof CreditCard);
    }

    /**
     * Persists a few User entities with associated BillingDetails. This test
     * then runs a query to retrieve one of the User objects. This test is
     * similar to lazilyLoadAUsersBillingDetails() and
     * lazilyLoadAUsersCreditCard(), but demonstrates how the lazy loading
     * process can be overridden to eagerly fetch the User's associated
     * BillingDetails (CreditCard) object at the time the User object is
     * loaded.
     */
    @Test
    public void eagerlyFetchAUsersBillingDetails() {
        // Populate database.
        entityManager.getTransaction().begin();
        populateDatabaseWithUsersAndBillingDetails();
        entityManager.getTransaction().commit();

        // Clear the persistence context, removing all managed objects.
        entityManager.clear();

        // Run a query to load a User object representing Amy.
        TypedQuery<User> query = entityManager
                .createQuery(
                        "select u from User u " +
                                "where u.username = :name", User.class)
                .setParameter("name", "amy");
        User user = query.getSingleResult();

        // Clear the persistence context. The persistent object referred to by
        // user is no longer managed. When the following query is executed, a
        // new instance representing Amy will be created and managed by the
        // persistence context.
        entityManager.clear();

        // Run a query to load User Amy. This query overrides the LAZY loading
        // property defined by the @ManyToOne association on BillingDetails in
        // class User. When loading the User object, the query eagerly fetches
        // the User's BillingDetails (CreditCard) object. Eager fetching is
        // specified using the "fetch" keyword in the JPQL query.
        TypedQuery<User> eagerFetchQuery = entityManager
                .createQuery(
                        "select u from User u " +
                                "left join fetch u.defaultBilling " +
                                "where u.id = :id", User.class)
                .setParameter("id", user.getId());
        user = eagerFetchQuery.getSingleResult();

        // Because the User object and its BillingDetails object is loaded
        // eagerly, calling getDefaultBillingDetails() doesn't return a proxy
        // but a CreditCard instance. There is no need for a proxy when eager
        // loading is used.
        CreditCard creditCard = (CreditCard) user.getDefaultBillingDetails();
        assertThat(creditCard.getClass().getName(), equalTo(CreditCard.class.getName()));
    }

    /**
     * Persists an Item entity without an associated Buyer. This test runs a
     * query to load the Item and checks that its Buyer field is null.
     */
    @Test
    public void persistItemWithoutBuyer() {
        // Start a transaction to persist an Item.
        entityManager.getTransaction().begin();

        Item dvd = new Item("American Sniper DVD");
        entityManager.persist(dvd);
        Long dvdId = dvd.getId();

        entityManager.getTransaction().commit();

        // Run a query to fetch the Item. The persistent context hasn't been
        // cleared, and so the query should return a reference to the original
        // Item instance that is still managed.
        TypedQuery<Item> queryItem = entityManager
                .createQuery("select item from Item item where item.id = :id", Item.class)
                .setParameter("id", dvdId);
        Item fetchedItem = queryItem.getSingleResult();
        assertSame(dvd, fetchedItem);
        assertNull(fetchedItem.getBuyer());
    }

    /**
     * Persists an Item entity that is associated with a User (buyer) entity.
     * The User has an Address ValueType and an association to a CreditCard
     * entity. This test checks that when the Item is retrieved from the
     * database that it is linked to the other objects as expected.
     */
    @Test
    public void persistItemWithBuyer() {
        // Create a transaction to persist an Item with an associated User
        // (buyer).
        entityManager.getTransaction().begin();

        Item dvd = new Item("American Sniper DVD");

        BillingDetails billing = new CreditCard("NASA", "4999...", "Apr-2015");

        User neil = new User("neil", "Armstrong", "Neil");
        neil.setDefaultBillingDetails(billing);
        neil.setAddress(User.AddressType.SHIPPING, new Address("Small Crater", "The Moon", "0000"));
        neil.setAddress(User.AddressType.BILLING, new Address("Small Crater", "The Moon", "0000"));
        neil.setAddress(User.AddressType.HOME, new Address("Small Crater", "The Moon", "0000"));

        dvd.setBuyer(neil);
        neil.addBoughtItem(dvd);

        // Note the ordering - to prevent database integrity violations.
        entityManager.persist(billing);
        entityManager.persist(neil);
        entityManager.persist(dvd);
        Long dvdId = dvd.getId();

        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Run a query to return the persisted User. Since the persistence
        // context has been cleared, the returned object should be a copy of
        // the persisted object.
        TypedQuery<Item> queryItem = entityManager
                .createQuery("select item from Item item where item.id = :id", Item.class)
                .setParameter("id", dvdId);
        Item fetchedItem = queryItem.getSingleResult();
        assertEquals(dvd, fetchedItem);

        // Check that the Buyer was persisted as expected.
        User buyer = fetchedItem.getBuyer();
        assertEquals(neil.getUserName(), buyer.getUserName());

        // Check that the Buyer's associated BillingDetails object was
        // persisted.
        BillingDetails bd = buyer.getDefaultBillingDetails();
        assertEquals(billing, bd);
    }

    /**
     *
     */
    @Test
    public void persistCategoriesAndItems() {
        // Create a transaction within which to persist a many-to-many
        // structure.
        entityManager.getTransaction().begin();

        Category sport = new Category("Sport");
        Category bicycles = new Category("Bicycles");

        Item triathleteBike = new Item("Litespeed Triathlete");
        Item kidsBike = new Item("Chopper");

        sport.addItem(triathleteBike);
        bicycles.addItem(triathleteBike);
        bicycles.addItem(kidsBike);

        triathleteBike.addCategory(sport);
        triathleteBike.addCategory(bicycles);
        kidsBike.addCategory(bicycles);

        entityManager.persist(sport);
        entityManager.persist(bicycles);

        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Load all Categories.
        TypedQuery<Category> query = entityManager
                .createQuery("select c from Category c", Category.class);
        List<Category> categories = query.getResultList();
        assertEquals(2, categories.size());

        // Check that Categories and Items are linked correctly.
        for (Category category : categories) {
            if (category.getName().equals("Sport")) {
                Item item = category.getItems().iterator().next();
                // "Sport" Category and "Litespeed Triathlete" Item should be
                // linked bi-directionally.
                assertEquals("Litespeed Triathlete", item.getName());
                assertTrue(item.getCategories().contains(category));
            } else {
                // "Bicycles" Category should contain 2 Items:
                // "Litespeed Triathlete" and "Chopper".
                assertEquals("Bicycles", category.getName());
                assertEquals(2, category.getItems().size());
                Iterator<Item> i = category.getItems().iterator();
                while (i.hasNext()) {
                    Item item = i.next();
                    // The "Bicycles" Category should be bi-directionally
                    // linked to the "Litespeed Triathlete" Item. Likewise for
                    // the "Chopper" Item.
                    assertTrue(item.getName().equals("Litespeed Triathlete") || item.getName().equals("Chopper"));
                    assertTrue(item.getCategories().contains(category));
                }
            }
        }

    }

    /**
     * Attempt to persist a Bid entity that is associated with a transient
     * (i.e. non persisted) Item entity. The attempt should fail for two
     * reasons:
     * 1. The Bid entity refers to a Item entity that is essentially
     * unmanaged. The Item entity isn't stored in the database.
     * 2. The Bid table has a 'not null' constraint on its foreign key column
     * ITEM_ID.
     */
    @Test
    public void attemptToPersistBidWithATransientItem() {
        entityManager.getTransaction().begin();

        Item dvd = new Item("American Sniper DVD");
        Bid bid = new Bid(dvd, new BigDecimal(10.00));

        // Attempt to save the Bid. This will fail because the Bid refers to a
        // transient Item object (the Item isn't yet persistent).
        try {
            entityManager.persist(bid);
            fail();
        } catch (Exception e) {
            // No action required.
        } finally {
            entityManager.getTransaction().rollback();
        }
    }

    /**
     * Persists a Bid entity independently of its associated Item entity. Other
     * tests, e.g. persistItemWithBids, demonstrate cascading persistence,
     * where saving an Item also saves its associated Bids.
     */
    @Test
    public void persistBidWithAPersistentItem() {
        // Create a transaction to create and save an Item.
        entityManager.getTransaction().begin();
        Item item = new Item("American Sniper DVD");
        entityManager.persist(item);
        Long itemId = item.getId();
        entityManager.getTransaction().commit();

        // Create another transaction to create and persist a Bid for the Item.
        entityManager.getTransaction().begin();
        Bid bid = new Bid(item, new BigDecimal(10.00));
        entityManager.persist(bid);
        Long bidId = bid.getId();
        entityManager.getTransaction().commit();

        // Clear the persistence context - the Item and Bid objects above will
        // no longer be managed.
        entityManager.clear();

        // Run a query to retrieve the Item entity from the database.
        TypedQuery<Item> itemQuery = entityManager
                .createQuery("select item from Item item where item.id = :id", Item.class)
                .setParameter("id", itemId);
        Item itemCopy = itemQuery.getSingleResult();

        // Check that the Item object is a copy of the original (now unmanaged)
        // Item object.
        assert (item != itemCopy);
        assertEquals(itemId, itemCopy.getId());

        // Check that the retrieved Item object contains the Bid.
        assertEquals(bid.getId(), itemCopy.getBids().iterator().next().getId());

        // Clear the persistence context.
        entityManager.clear();

        // Run a query to retrieve the Bid entity from the database.
        TypedQuery<Bid> bidQuery = entityManager
                .createQuery("select bid from Bid bid where bid.id = :id", Bid.class)
                .setParameter("id", bidId);
        Bid bidCopy = bidQuery.getSingleResult();

        // Check that the Bid is retrieved and that it has the same database
        // identity as the original Bid.
        assertEquals(bid.getId(), bidCopy.getId());
    }

    /**
     * Persists a number of Items with Bids.This method then performs a query
     * that returns the Cartesian product of all Items and Bids.
     */
    @Test
    public void performBasicProjectionQuery() {
        // Create a transaction in which to persist 3 Items and 3 Bids.
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        entityManager.getTransaction().commit();

        // Run a query to return the Cartesian product of all persisted Items
        // and Bids. The query returns a List of arrays. Each array contains 2
        // elements - an Item and a Bid pair.
        TypedQuery<Object[]> query = entityManager.createQuery("select i, b from Item i, Bid b", Object[].class);
        List<Object[]> result = query.getResultList();

        Set<Item> items = new HashSet<Item>();
        Set<Bid> bids = new HashSet<Bid>();
        List<Object> allObjects = new ArrayList<Object>();

        // Iterate through the result - a List of arrays.
        for (Object[] row : result) {
            // Check that the first element of the array is an Item.
            assertTrue(row[0] instanceof Item);
            _logger.info("Item: " + row[0]);
            items.add((Item) row[0]);

            // Check that the second element of the array is a Bid.
            assertTrue(row[1] instanceof Bid);
            _logger.info("Bid: " + row[1] + "\n");
            bids.add((Bid) row[1]);

            // Add Item and Bid objects to allObjects. allObjects stores each
            // instance at most once.
            if (!containsReferenceTo(allObjects, row[0])) {
                allObjects.add(row[0]);
            }
            if (!containsReferenceTo(allObjects, row[1])) {
                allObjects.add(row[1]);
            }
        }

        // The query should return the Cartesian product of 3 Items and 3 Bids,
        // hence 9 entries.
        assertEquals(9, result.size());

        // 3 Items and 3 Bids were persisted in the database. 3 distinct Item
        // and 3 distinct Bid instances should be returned by the query -
        // despite it returning 9 Item/Bid pairs. The persistence context
        // guarantees that a database row is represented by no more than one
        // object. Hence the 9 Item/Bid pairs involve aliasing the 6 objects.
        assertEquals(6, allObjects.size());

        // The items and bids Sets should each hold 3 distinct instances.
        assertEquals(3, items.size());
        assertEquals(3, bids.size());
    }

    /**
     * Demonstrates how to write a JPQL query using an implicit association
     * join. This method persists Items and Bids and then runs a query that
     * involves a join between the Bid and Item tables. The join is expressed
     * implicitly as a path expression naming Bid's _item association.
     */
    @Test
    public void performImplicitJoinQuery() {
        // Create a transaction in which to persist 3 Items and 3 Bids.
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Run a query to return all the Bids for Items whose named includes
        // 'DVD'.
        TypedQuery<Bid> queryForBid = entityManager.createQuery(
                "select b from Bid b where b.item.name like '%DVD%'", Bid.class);
        List<Bid> queryForBidResult = queryForBid.getResultList();

        // The query should 2 Bid instances.
        assertEquals(2, queryForBidResult.size());

        // Run a query to return all Items who name is 'American Sniper DVD'.
        // This should return a single Item instance.
        TypedQuery<Item> queryForItem = entityManager.createQuery(
                "select i from Item i where i.name = 'American Sniper DVD'", Item.class);
        Item queryForItemResult = queryForItem.getSingleResult();

        // The 2 Bids should refer to the Item instance.
        assertSame(queryForItemResult, queryForBidResult.get(0).getItem());
        assertSame(queryForItemResult, queryForBidResult.get(1).getItem());
    }

    /**
     * Illustrates how to write a JPQL query using an explicit join. This test
     * persists Items and Bids and runs an inner join query to select all Item
     * objects and their Bids, and then uses restriction to return only Items
     * that have Bids exceeding $100.
     */
    @Test
    public void performExplicitInnerJoinQuery() {
        // Create a transaction in which to persist 3 Items and 3 Bids.
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Run the query.
        TypedQuery<Item> queryForItem = entityManager.createQuery(
                "select i from Item i " +
                        "join i.bids b " +
                        "where b.amount > 100", Item.class);
        List<Item> queryForItemResult = queryForItem.getResultList();

        for (Item i : queryForItemResult) {
            _logger.info("Got: " + i.toString());
        }

        // The query should return a single Item - the iPhone. This is the only
        // Item that has a Bid in excess of $100.
        assertEquals(1, queryForItemResult.size());
        assertEquals("iPhone", queryForItemResult.get(0).getName());
    }

    /**
     * Illustrates how to write a JPQL query using an explicit join. This test
     * persists Items and Bids and runs a left outer join query to return ALL
     * Items and any associated Bids that exceed $100. The query returns
     * Item/Bid pairs; for Items that don't have any Bids exceeding $100 the
     * Bid value is null.
     */
    @Test
    public void performExplicitLeftOuterJoinQuery() {
        // Create a transaction in which to persist 3 Items and 3 Bids.
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        TypedQuery<Object[]> queryForItemBidPairs = entityManager.createQuery(
                "select i, b from Item i " +
                        "left join i.bids b on b.amount > 100", Object[].class);
        List<Object[]> queryForItemBidPairsResult = queryForItemBidPairs.getResultList();

        // The query should have returned 3 Item/Bid pairs.
        assertEquals(3, queryForItemBidPairsResult.size());

        Set<Item> items = new HashSet<Item>();
        Set<Bid> bids = new HashSet<Bid>();

        for (Object[] row : queryForItemBidPairsResult) {
            if (row[0] != null) {
                items.add((Item) row[0]);
            }
            if (row[1] != null) {
                bids.add((Bid) row[1]);
            }
            _logger.info("Item: " + row[0]);
            _logger.info("Bid: " + row[1]);
        }

        // The query should have retrieved ALL Item instances because of the
        // left outer join.
        assertEquals(3, items.size());

        // The query should have retrieved only a single Bid. The only Bid that
        // exceeds $100 is the Bid for the iPhone item.
        assertEquals(1, bids.size());
        assertEquals("iPhone", bids.iterator().next().getItem().getName());

        // Close the EntityManager - objects can no longer be lazily loaded.
        entityManager.close();

        // Attempt to access Bids. Since the EntityManager has been closed and
        // Item's Bids are loaded lazily, an attempt to access Bids that have
        // have not yet been loaded fails.
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            try {
                Iterator<Bid> bidsIterator = item.getBids().iterator();
                fail();
            } catch (LazyInitializationException e) {
                // No action needed - the exception is expected.
            }
        }
    }

    /**
     * Illustrates how to write a JPQL query using an explicit join. This test
     * is similar to performExplicitLeftOuterJoinQuery(), but instead of
     * retrieving ALL Items, it retrieves only those Items that have Bids in
     * excess of $100. This method does this by running a similar query, but
     * instead of making the "amount > 100" part of the join condition, it
     * specifies it using a where clause. Hence only Items that are associated
     * with Bids greater than $100 appears in the result set.
     * <p>
     * The query run in this test is in effect an alternative way of expressing
     * the inner join query of test performExplicitInnerJoinQuery().
     */
    @Test
    public void performExplicitLeftOuterJoinWithWhereClauseQuery() {
        // Create a transaction in which to persist 3 Items and 3 Bids.
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Run the query.
        TypedQuery<Object[]> queryForItemBidPairs = entityManager.createQuery(
                "select i, b from Item i " +
                        "left join i.bids b on b.item.id = i.id " +
                        "where b.amount > 100", Object[].class);
        List<Object[]> queryForItemBidPairsResult = queryForItemBidPairs.getResultList();

        // The query should only return 1 Item/Bid pair. The Item should be the
        // iPhone and the Bid should be its Bid that exceeds $100.
        assertEquals(1, queryForItemBidPairsResult.size());
        for (Object[] row : queryForItemBidPairsResult) {
            Item item = (Item) row[0];
            assertEquals("iPhone", item.getName());

            Bid bid = (Bid) row[1];
            assertSame(item, bid.getItem());

            _logger.info("Item: " + row[0]);
            _logger.info("Bid: " + row[1]);
        }
    }

    /**
     * Illustrates how to write a JPQL query using an explicit join. This test
     * persists Items and Bids and runs a right outer join query to return ALL
     * Items and any associated Bids that exceed $100. The query returns
     * Bid/Item pairs; for Items that don't have any Bids exceeding $100 the
     * Bid value is null.
     * <p>
     * This test does the same thing as performExplicitLeftOuterJoinQuery().
     * The difference, however, is that this test uses a right join query.
     * Right join queries are necessary when a collection is mapped with only a
     *
     * @ManyToOne annotation (i.e. there is no corresponding @OneToMany
     * annotation). For this application, the join condition can't be expressed
     * in terms of an Item's _bids property, because in the absence of a
     * @OneToMany annotation _bids doesn't exist.
     */
    @Test
    public void performExplicitRightOuterJoinQuery() {
        // Create a transaction in which to persist 3 Items and 3 Bids.
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Run the query.
        TypedQuery<Object[]> queryForBidItemPairs = entityManager.createQuery(
                "select b, i from Bid b " +
                        "right outer join b.item i on b.amount > 100", Object[].class);
        List<Object[]> queryForBidItemPairsResult = queryForBidItemPairs.getResultList();

        // The query should have returned 3 Bid/Item pair references.
        assertEquals(3, queryForBidItemPairsResult.size());

        Set<Item> items = new HashSet<Item>();
        Set<Bid> bids = new HashSet<Bid>();

        for (Object[] row : queryForBidItemPairsResult) {
            if (row[0] != null) {
                bids.add((Bid) row[0]);
            }
            if (row[1] != null) {
                items.add((Item) row[1]);
            }
            _logger.info("Bid: " + row[0]);
            _logger.info("Item: " + row[1]);
        }

        // The query should have retrieved only a single Bid instance. The only
        // Bid that exceeds $100 is the Bid for the iPhone item.
        assertEquals(1, bids.size());
        assertEquals("iPhone", bids.iterator().next().getItem().getName());

    }

    /**
     * Illustrates how to write a JPQL query using an explicit join. This test
     * persists Items and Bids and runs a right outer join query to return only
     * Items that have associated Bids in excess of $100. The query returns
     * Bid/Item pairs.
     * <p>
     * This test does the same thing as
     * performExplicitLeftOuterJoinWithWhereClauseQuery(). The difference,
     * however, is that this test uses a right join query. Right join queries
     * are necessary when a collection is mapped with only a @ManyToOne
     * annotation (i.e. there is no corresponding @OneToMany annotation). For
     * this application, the join condition can't be expressed in terms of an
     * Item's _bids property, because in the absence of a @OneToMany annotation
     * _bids doesn't exist.
     */
    @Test
    public void performExplicitRightOuterJoinWithWhereClauseQuery() {
        // Create a transaction in which to persist 3 Items and 3 Bids.
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        entityManager.getTransaction().commit();

        // Clear the persistence context.
        entityManager.clear();

        // Run the query.
        TypedQuery<Object[]> queryForBidItemPairs = entityManager.createQuery(
                "select b, i from Bid b " +
                        "right outer join b.item i " +
                        "where b.amount > 100", Object[].class);
        List<Object[]> queryForBidItemPairsResult = queryForBidItemPairs.getResultList();

        // The query should only return 1 Bid/Item pair. The Item should be the
        // iPhone and the Bid should be its Bid that exceeds $100.
        for (Object[] row : queryForBidItemPairsResult) {
            Bid bid = (Bid) row[0];
            Item item = (Item) row[1];

            assertEquals("iPhone", item.getName());
            assertSame(item, bid.getItem());

            _logger.info("Bid: " + row[0]);
            _logger.info("Item: " + row[1]);

        }
    }

    /**
     * Demonstrates how to run a query involving a join and eager fetching.
     * This test persists a number of Items and associated Bids and then runs a
     * query to retrieve all Items and eagerly fetch their associated Bids.
     */
    @Test
    public void performEagerFetchingQuery() {
        // Create a transaction in which to persist 3 Items and 3 Bids.
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Run the query.
        TypedQuery<Item> query = entityManager.createQuery(
                "select i from Item i " +
                        "left join fetch i.bids", Item.class);
        List<Item> result = query.getResultList();

        // There are only 3 Items persisted, but the query actually returns 4
        // Items. This is because there are 2 Bids for one of the Items. JPA
        // thus preserves the number of rows returned in the SQL result set.
        assertEquals(4, result.size());

        // The query result contains 2 references to the Item that has 2 Bids.
        // The duplicate references can be filtered by passing the result
        // through a Set. A LinkedHashSet preserves order.
        Set<Item> distinctResult = new LinkedHashSet<Item>(result);
        assertEquals(3, distinctResult.size());

        // Close the EntityManager. This prevents lazy loading of objects.
        entityManager.close();

        // Iterate through the Items and their Bids. Since the EntityManager
        // has been closed, only loaded persistent objects can be operated on.
        // The query for Items eagerly fetched associated Items, so the Bids
        // for each Item should be in memory.
        for (Item item : result) {
            try {
                Iterator<Bid> iterator = item.getBids().iterator();
                while (iterator.hasNext()) {
                    Bid bid = iterator.next();
                    _logger.info(bid.toString());
                }
            } catch (LazyInitializationException e) {
                fail();
            }
        }
    }

    /**
     * Runs a couple of simple queries using restriction to:
     * 1. Retrieve all Items that have names including the text "DVD".
     * 2. Return all Bids for a specified Item where the Bid values are less
     * than $100.
     */
    @Test
    public void performRestrictionQueries() {
        // Create a transaction in which to persist 3 Items and 3 Bids.
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndBids();
        entityManager.getTransaction().commit();

        entityManager.clear();

        // This query should retrieve a single Item.
        TypedQuery<Item> queryForItem = entityManager
                .createQuery("select i from Item i where i.name like '%DVD%'", Item.class);
        Item item = queryForItem.getSingleResult();

        // This query finds all the Bids for the loaded Item where the Bids are
        // less than $100.
        TypedQuery<Bid> queryBids = entityManager
                .createQuery("select bid from Bid bid where bid.item = :item and bid.amount < 100.00", Bid.class)
                .setParameter("item", item);

        List<Bid> queryResultBids = queryBids.getResultList();
        assertEquals(2, queryResultBids.size());

        TypedQuery<Item> query = entityManager
                .createQuery("select i from Item i where i.id = :id", Item.class)
                .setParameter("id", item.getId());
        query.getSingleResult();
    }

    /**
     * Illustrates use of the Criteria API for programmatically creating
     * queries.
     */
    @Test
    public void criteriaQuery() {
        entityManager.getTransaction().begin();
        populateDatabaseWithItemsAndImages();
        entityManager.getTransaction().commit();

        // Create a CriteriaBuilder.
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Build a query for: "from Item".
        CriteriaQuery<Item> criteria = cb.createQuery(Item.class);
        criteria.select(criteria.from(Item.class));

        // Use the CriteriaQuery to create a TypedQuery.
        TypedQuery<Item> query = entityManager.createQuery(criteria);
        List<Item> items = query.getResultList();
        assertEquals(3, items.size());

        // Build a query for: "from Item where _name = 'iPhone'".
        criteria = cb.createQuery(Item.class);
        Root<Item> i = criteria.from(Item.class);
        criteria.select(i).where(
                cb.equal(i.get("name"), "iPhone")
        );
        query = entityManager.createQuery(criteria);
        items = query.getResultList();
        assertEquals(1, items.size());
    }

    /**
     * Helper method to populate the database.
     */
    protected void populateDatabaseWithItemsAndImages() {
        // Creates Items with embedded Images.
        Item dvd = new Item("American Sniper DVD");
        dvd.addImage(new Image("DVD", "image_675.png", 640, 480));
        dvd.addImage(new Image("DVD case", "image_676.png", 800, 600));
        dvd.addImage(new Image("DVD booklet", "image_678.png", 1024, 768));
        Item iPhone = new Item("iPhone");
        iPhone.addImage(new Image("DVD", "iPhone.jpg", 640, 480));
        Item speaker = new Item("Bluetooth speaker");

        // Persist Items. Since Images are ValueTypes, they are automatically
        // persisted when their owning entities (Items) are persisted.
        entityManager.persist(dvd);
        entityManager.persist(iPhone);
        entityManager.persist(speaker);
    }

    /**
     * Helper method to populate the database.
     */
    protected void populateDatabaseWithItemsAndBids() {
        Item dvd = new Item("American Sniper DVD");
        dvd.addImage(new Image("DVD", "image_675.png", 640, 480));
        dvd.addImage(new Image("DVD case", "image_676.png", 800, 600));
        dvd.addImage(new Image("DVD booklet", "image_678.png", 1024, 768));

        Item iPhone = new Item("iPhone");
        iPhone.addImage(new Image("DVD", "iPhone.jpg", 640, 480));

        Item speaker = new Item("Bluetooth speaker");

        entityManager.persist(dvd);
        entityManager.persist(iPhone);
        entityManager.persist(speaker);

        Bid bidOne = new Bid(dvd, new BigDecimal(18.50));
        dvd.addBid(bidOne);

        Bid bidTwo = new Bid(iPhone, new BigDecimal(200.00));
        iPhone.addBid(bidTwo);

        Bid bidThree = new Bid(dvd, new BigDecimal(22.00));
        dvd.addBid(bidThree);
    }

    /**
     * Helper method to populate the database.
     */
    protected void populateDatabaseWithUsersAndBillingDetails() {
        BillingDetails billing = new CreditCard("Amy", "4999...", "Apr-2015");
        User amy = new User("amy", "Johnson", "Amy");
        amy.setDefaultBillingDetails(billing);
        amy.setAddress(User.AddressType.SHIPPING, new Address("Sydney Gardens", "Auckland", "1010"));
        amy.setAddress(User.AddressType.BILLING, new Address("Sydney Gardens", "Auckland", "1010"));
        amy.setAddress(User.AddressType.HOME, new Address("Sydney Gardens", "Auckland", "1010"));

        User neil = new User("neil", "Armstrong", "Neil");
        neil.setDefaultBillingDetails(billing);
        neil.setAddress(User.AddressType.SHIPPING, new Address("Small Crater", "The Moon", "0000"));
        neil.setAddress(User.AddressType.BILLING, new Address("Small Crater", "The Moon", "0000"));
        neil.setAddress(User.AddressType.HOME, new Address("Small Crater", "The Moon", "0000"));


        User felix = new User("felix", "Baumgartner", "Felix");
        felix.setAddress(User.AddressType.SHIPPING, new Address("Balloon", "Edge of space", "0000"));
        felix.setAddress(User.AddressType.BILLING, new Address("Balloon", "Edge of space", "0000"));
        felix.setAddress(User.AddressType.HOME, new Address("Balloon", "Edge of space", "0000"));


        BillingDetails otherbilling = new CreditCard("Kim", "4999...", "Mar-2017");

        entityManager.persist(billing);
        entityManager.persist(amy);
        entityManager.persist(neil);
        entityManager.persist(felix);
        entityManager.persist(otherbilling);
    }

    /**
     * Returns {@code true} if the collection contains the specified element.
     * <p>
     * More formally, returns {@code true} if and only if this collection
     * contains at least one element {@code x} such that {@code x == element}.
     * <p>
     * Note: {@link Collection#contains(Object)} works differently because uses
     * {@link Object#equals(Object)} for comparison
     *
     * @param collection collection where to look for the element
     * @param element    element whose presence in this collection is to be tested
     * @return {@code true} if this collection contains the specified element
     * @throws NullPointerException if {@code collection} is null
     */
    protected static <T> boolean containsReferenceTo(Collection<T> collection,
                                                     T element) {
        for (T x : collection) {
            if (x == element) {
                return true;
            }
        }
        return false;
    }
}
