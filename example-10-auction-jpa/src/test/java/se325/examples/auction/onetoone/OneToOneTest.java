package se325.examples.auction.onetoone;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.examples.auction.JpaTest;

/**
 * Test class to illustrate the behaviour of JPA in generating relational
 * schemas and SQL, and with more generally managing object persistence.
 * <p>
 * Most of the tests are implemented in class nz.ac.auckland.domain.DomainTest.
 * This class (OneToOneTest) is an extra test class that illustrates the effect
 * of @OneToOne declarations, used in package nz.ac.auckland.domain.onetoone.
 * The one-to-one relationships are covered in a separate package because
 * different versions of existing domain model classes are required (the
 * nz.ac.auckland.domain. classes are annotated differently, e.g. with
 *
 * @author Ian Warren
 * @OneToMany, @ManyToOne etc.).
 * <p>
 * This class inherits from JpaTest, which manages database connectivity,
 * JPA initialisation and takes care of clearing out the database immediately
 * prior to executing each unit test. This ensures that there are no side-
 * effects of running any tests.
 */
public class OneToOneTest extends JpaTest {

    private static Logger _logger = LoggerFactory.getLogger(OneToOneTest.class);

    /**
     * Demonstrates the effect of a @OneToOne mapping where the USERS table
     * contains a foreign key constrained column to link to the ADDRESS table.
     */
    @Test
    public void establishOneTopOneRelationshipUsingForeignKeyJoinColumn() {
        entityManager.getTransaction().begin();

        User_onetoone neil = new User_onetoone("neil", "Armstrong", "Neil");
        Address_onetoone moon = new Address_onetoone("Small Crater", "The Moon", "0000");
        neil.setShippingAddress(moon);

        User_onetoone felix = new User_onetoone("felix", "Baumgartner", "Felix");
        Address_onetoone space = new Address_onetoone("Balloon", "Edge of space", "0000");
        felix.setShippingAddress(space);

        // Store Users, transitively persisting the Addresses.
        entityManager.persist(neil);
        entityManager.persist(felix);

        entityManager.getTransaction().commit();
    }

    /**
     * Demonstrates the effect of a @OneToOne mapping where an intermediate
     * join table is included in the relational schema. This is better for
     * implementing optional one-to-one associations as it avoids the storage
     * of null values.
     */
    @Test
    public void establishOneToOneOptionalRelationshipUsingJoinTable() {
        entityManager.getTransaction().begin();

        // Create a Shipment without an Item and persist it.
        Shipment_onetoone shipment = new Shipment_onetoone();
        entityManager.persist(shipment);

        // Create and persist an Item.
        Item_onetoone item = new Item_onetoone("1984 Ford Capri");
        entityManager.persist(item);

        // Create a Shipment with an Item and persist it.
        Shipment_onetoone auctionShipment = new Shipment_onetoone(item);
        entityManager.persist(auctionShipment);

        entityManager.getTransaction().commit();
    }

}