package se325.examples.auction.onetomany;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.examples.auction.JpaTest;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test class to illustrate the behaviour of JPA in generating relational
 * schemas and SQL, and with more generally managing object persistence.
 * <p>
 * Most of the tests are implemented in class nz.ac.auckland.domain.DomainTest.
 * This class (OneToManyTest) is an extra test class that illustrates the
 * effect of a @OneToMany declaration (without a corresponding @ManyToOne
 * annotation). The one-to-many relationship is covered in a separate package
 * (nz.ac.auckland.domain.onetomany) because different versions of existing
 * domain model classes are required.
 * <p>
 * This class inherits from JpaTest, which manages database connectivity,
 * JPA initialisation and takes care of clearing out the database immediately
 * prior to executing each unit test. This ensures that there are no side-
 * effects of running any tests.
 *
 * @author Ian Warren
 */
public class OneToManyTest extends JpaTest {

    private static Logger _logger = LoggerFactory.getLogger(OneToManyTest.class);

    /**
     * Persists an Item entity that is associated with Bids. The mapping for the
     * Bids collection within Item specifies cascading persistence so that when
     * an Item is saved, so too are its Bids.
     */
    @Test
    public void persistItemWithBids() {
        entityManager.getTransaction().begin();

        Item_onetomany item = new Item_onetomany("Spanner");
        Bid_onetomany bid = new Bid_onetomany(new BigDecimal(10.00));
        Bid_onetomany bid2 = new Bid_onetomany(new BigDecimal(12.00));
        item.addBid(bid);
        item.addBid(bid2);

        entityManager.persist(item);
        Long itemId = item.getId();
        Set<Long> bidIds = new HashSet<Long>();
        bidIds.add(bid.getId());
        bidIds.add(bid2.getId());

        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();

        // Clear the persistence context - the Item and Bid objects above will
        // no longer be managed.
        entityManager.clear();

        // Run a query to retrieve the Item entity from the database.
        TypedQuery<Item_onetomany> itemQuery = entityManager
                .createQuery("select item from Item_onetomany item where item.id = :id", Item_onetomany.class)
                .setParameter("id", itemId);
        Item_onetomany itemCopy = itemQuery.getSingleResult();

        // The query should retrieve a new Item entity representing the
        // original Item.
        assertNotSame(item, itemCopy);
        assertEquals(itemId, itemCopy.getId());

        // The retrieved Item should store references to its two Bid entities.
        Iterator<Bid_onetomany> iterator = itemCopy.getBidsIterator();
        int count = 0;
        while (iterator.hasNext()) {
            Bid_onetomany b = iterator.next();
            assertTrue(bidIds.contains(b.getId()));
            count++;
        }
        assertEquals(2, count);

        // Clear the persistence context.
        entityManager.clear();

        // Run a query to return all Bid data for Bids associated with the Item
        // entity. This example illustrates how to use the JPA EntityManager to
        // run a native SQL query. Since the association between Item and Bid
        // is unidirectional (from Item to Bid), Bid doesn't have a link to its
        // Item. The link is however is stored in the database as a foreign key
        // constraint in the BIDS table.
        Query bidsQuery = entityManager
                .createNativeQuery("select ID, ITEM_ID, AMOUNT from BID_ONETOMANY where ITEM_ID =  :itemId")
                .setParameter("itemId", itemId);
        @SuppressWarnings("unchecked")
        List<Object[]> bids = bidsQuery.getResultList();

        // The query should include 2 IDs that match the database identities of
        // the original Bid entities persisted. Pull the IDs out of the SQL
        // result-set and check.
        assertEquals(2, bids.size());
        for (Object[] row : bids) {
            Long columnValue = new Long(((BigInteger) (row[0])).longValue());
            assertTrue(bidIds.contains(columnValue));
        }
    }

    /**
     * Attempts to persist a Bid entity that is not associated with an Item
     * entity. This should fail because the mapping for the Bids collection in
     * class Item specifies that the foreign key column for the Bid table has a
     * non-null constraint.
     */
    @Test
    public void attemptToPersistIsolatedBid() {
        entityManager.getTransaction().begin();
        Bid_onetomany bid = new Bid_onetomany(new BigDecimal(10.00));

        try {
            entityManager.persist(bid);
            entityManager.getTransaction().commit();
            fail();
        } catch (Exception e) {
            // No action required.
        }
    }
}
