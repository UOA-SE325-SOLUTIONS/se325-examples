package se325.examples.auction;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;

/**
 * Test superclass with useful pre/post test processing behaviour.
 *
 * @author Ian Warren
 */
public abstract class JpaTest {

//    private static Logger LOGGER = LoggerFactory.getLogger(DomainTest.class);

    // JPA EntityManagerFactory, used to create an EntityManager.
    protected static EntityManagerFactory factory = null;

    // JPA EntityManager, which provides transactional and persistence
    // operations.
    protected EntityManager entityManager = null;

    /**
     * One-time setup method for all test cases.
     * <p>
     * Initialises the database by dropping any existing tables prior to
     * creating a JPA EntityManagerFactory. When the JPA EntityManagerFactory
     * is created, it extracts metadata from domain model classes and creates
     * the necessary database tables.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @BeforeClass
    public static void initialiseDatabase() throws ClassNotFoundException,
            SQLException {

        // Delete the test DB file before running anything.
        DatabaseUtility.deleteDatabase();

        // Create the JPA EntityManagerFactory.
        factory = Persistence.createEntityManagerFactory("se325.examples.auction");
    }

    /**
     * One-time finalisation method for all test cases. This method releases
     * the JDBC database connection.
     *
     * @throws SQLException
     */
    @AfterClass
    public static void releaseEntityManagerFactory() throws SQLException {
        factory.close();
    }

    /**
     * Immediately before each test cases runs, this method runs to remove any
     * rows in database tables. This ensures that each test begins with an
     * empty database. In addition it creates a new entityManager for each test.
     *
     * @throws SQLException
     */
    @Before
    public void clearDatabase() throws SQLException, ClassNotFoundException {
        // Delete all rows from any existing database tables.
        DatabaseUtility.clearTables();

        // Create the JPA EntityManager.
        entityManager = factory.createEntityManager();
    }

    /**
     * Immediately after each test case runs, this method releases the JPA
     * EntityManager used by the test.
     */
    @After
    public void closeEntityManager() throws SQLException {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
