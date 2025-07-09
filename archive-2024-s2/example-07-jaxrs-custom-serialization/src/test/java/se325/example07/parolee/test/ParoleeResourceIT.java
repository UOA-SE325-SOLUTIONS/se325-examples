package se325.example07.parolee.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.example07.parolee.domain.Gender;
import se325.example07.parolee.domain.Parolee;
import se325.example07.parolee.services.SerializationMessageBodyReaderAndWriter;

/**
 * Simple JUnit test to test the behaviour of the Parolee Web service.
 * <p>
 * The test is implemented using the JAX-RS client API.
 */
public class ParoleeResourceIT {

    private static Logger _logger = LoggerFactory
            .getLogger(ParoleeResourceIT.class);

    private static String WEB_SERVICE_URI = "http://localhost:10000/services/parolees";

    private static Client client;

    private static Parolee[] PAROLEE_PAYLOADS = {
            new Parolee(null, "Al", "Capone", Gender.MALE, "1899-01-17"),
            new Parolee(null, "John", "Gotti", Gender.MALE, "1940-10-27"),
            new Parolee(null, "Pablo", "Escobar", Gender.MALE, "1949-12-01"),
            new Parolee(null, "Carlos", "Marcello", Gender.MALE, "1910-02-06")
    };

    private static List<String> paroleeUris = new ArrayList<>();


    @BeforeClass
    public static void createClient() {
        // Use ClientBuilder to create a new client that can be used to create
        // connections to the Web service.
        client = ClientBuilder
                .newBuilder()
                .register(SerializationMessageBodyReaderAndWriter.class)
                .build();
    }

    @AfterClass
    public static void closeConnection() {
        client.close();
    }

    @Before
    public void clearAndPopulate() {
        // Delete all Parolees in the Web service.
        try (Response response = client.target(WEB_SERVICE_URI).request().delete()) {
        }

        // Clear Parolee Uris
        paroleeUris.clear();

        // Populate the service with Parolees.
        for (Parolee payload : PAROLEE_PAYLOADS) {
            try (Response response = client.target(WEB_SERVICE_URI)
                    .request()
                    .post(Entity.entity(payload, "application/java-serialization"))) {
                String paroleeUri = response.getLocation().toString();
                paroleeUris.add(paroleeUri);
            }
        }
    }

    @Test
    public void testCreate() {
        // New parolee
        Parolee payload = new Parolee(null, "Jesse", "James", Gender.MALE, "1847-09-05");

        // Make a HTTP POST request to create a new Parolee.
        try (Response response = client.target(WEB_SERVICE_URI)
                .request()
                .post(Entity.entity(payload, "application/java-serialization"))) {

            // Check that the HTTP response code is 201 Created.
            int responseCode = response.getStatus();
            assertEquals(201, responseCode);

            String paroleeUri = response.getLocation().toString();
            _logger.info("Uri of newly created Parolee: " + paroleeUri);

        }
    }

    @Test
    public void testRetrieve() throws IOException {
        String paroleeUri = paroleeUris.get(paroleeUris.size() - 1);

        // Make a HTTP GET request to retrieve the last created Parolee.
        try (Response response = client.target(paroleeUri)
                .request()
                .accept("application/java-serialization")
                .get()) {

            // Check that the HTTP response code is 200 OK.
            int responseCode = response.getStatus();
            assertEquals(200, responseCode);

            Parolee parolee = response.readEntity(Parolee.class);
            _logger.info("Retrieved Parolee: " + parolee);

        }

    }

    @Test
    public void testUpdate() {
        // Give Al Capone a gender change
        Parolee updateParolee = new Parolee(null, "Al", "Capone", Gender.FEMALE, "1899-01-17");

        // Make a HTTP PUT request to update the Parolee.
        try (Response response = client.target(WEB_SERVICE_URI + "/1")
                .request()
                .put(Entity.entity(updateParolee, "application/java-serialization"))) {

            // Check that the HTTP response code is 204 No content.
            int status = response.getStatus();
            assertEquals(204, status);

        }
    }

    @Test
    public void testDelete() {
        // Make a HTTP DELETE request to delete the first Parolee.
        try (Response response = client.target(WEB_SERVICE_URI + "/1")
                .request()
                .delete()) {

            // Check that the HTTP response code is 204 No content.
            int status = response.getStatus();
            assertEquals(204, status);

        }
    }
}