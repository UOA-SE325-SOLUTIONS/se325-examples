package se325.example05.parolee;

import static org.junit.Assert.assertEquals;

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

/**
 * Simple JUnit test to test the behaviour of the Parolee Web service.
 * <p>
 * The test is implemented using the JAX-RS client API.
 */
public class ParoleeResourceIT {

    private static Logger _logger = LoggerFactory.getLogger(ParoleeResourceIT.class);

    private static String WEB_SERVICE_URI = "http://localhost:10000/paroleeServices/parolees";

    private static Client client;

    private static String[] jsonPayloads = {
            "{ \"firstName\": \"Al\", \"lastName\": \"Capone\", \"gender\": \"MALE\", \"dateOfBirth\": \"1899-01-17\" }",

            "{ \"firstName\": \"John\", \"lastName\": \"Gotti\", \"gender\": \"MALE\", \"dateOfBirth\": \"1940-10-27\" }",

            "{ \"firstName\": \"Pablo\", \"lastName\": \"Escobar\", \"gender\": \"MALE\", \"dateOfBirth\": \"1949-12-01\" }",

            "{ \"firstName\": \"Carlos\", \"lastName\": \"Marcello\", \"gender\": \"MALE\", \"dateOfBirth\": \"1910-02-06\" }"
    };

    private static List<String> paroleeUris = new ArrayList<>();


    @BeforeClass
    public static void createClient() {
        // Use ClientBuilder to create a new client that can be used to create connections to the Web service.
        client = ClientBuilder.newClient();
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
        for (String payload : jsonPayloads) {
            try (Response response = client.target(WEB_SERVICE_URI).request().post(Entity.json(payload))) {
                String paroleeUri = response.getLocation().toString();
                paroleeUris.add(paroleeUri);
            }
        }
    }

    @Test
    public void testCreate() {
        // JSON representation of the new Parolee.
        String jsonPayload = "{ \"firstName\": \"Jesse\", \"lastName\": \"James\", \"gender\": \"MALE\", \"dateOfBirth\": \"1847-09-05\" }";

        // Make a HTTP POST request to create a new Parolee.
        try (Response response = client.target(WEB_SERVICE_URI).request().post(Entity.json(jsonPayload))) {

            // Check that the HTTP response code is 201 Created.
            int responseCode = response.getStatus();
            assertEquals(201, responseCode);

            String paroleeUri = response.getLocation().toString();
            _logger.info("Uri of newly created Parolee: " + paroleeUri);
        }
    }

    @Test
    public void testRetrieve() {

        String paroleeUri = paroleeUris.get(paroleeUris.size() - 1);

        // Make a HTTP GET request to retrieve the last created Parolee.
        try (Response response = client.target(paroleeUri).request().get()) {

            // Check that the HTTP response code is 200 OK.
            int responseCode = response.getStatus();
            assertEquals(200, responseCode);

            String jsonResponse = response.readEntity(String.class);
            _logger.info("Retrieved Parolee: " + jsonResponse);

        }
    }

    @Test
    public void testUpdate() {
        // Create a JSON representation of the first parolee, changing Al Capone's gender.
        String updateParolee = "{ \"firstName\": \"Al\", \"lastName\": \"Capone\", \"gender\": \"FEMALE\", \"dateOfBirth\": \"1899-01-17\" }";

        // Make a HTTP PUT request to update the Parolee.
        try (Response response = client.target(WEB_SERVICE_URI + "/1").request().put(Entity.json(updateParolee))) {

            // Check that the HTTP response code is 204 No content.
            int status = response.getStatus();
            assertEquals(204, status);

        }

    }

    @Test
    public void testDelete() {
        // Make a HTTP DELETE request to delete the first Parolee.
        try (Response response = client.target(WEB_SERVICE_URI + "/1").request().delete()) {

            // Check that the HTTP response code is 204 No content.
            int status = response.getStatus();
            assertEquals(204, status);

        }
    }
}