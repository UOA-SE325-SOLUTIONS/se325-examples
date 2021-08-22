package se325.example16.parolee.services;

import org.junit.After;
import org.junit.Before;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public abstract class BaseIntegrationTests {

    protected static final String WEB_SERVICE_URI = "http://localhost:10000/services/parolees";
    protected Client client;

    /**
     * Before each test, create a new client and reset the server
     */
    @Before
    public void setUpWebClient() {

        client = ClientBuilder.newClient();

        try (Response response = client
                .target(WEB_SERVICE_URI + "-test/reset-database").request()
                .put(null)) {

            assertEquals(204, response.getStatus());
        }
    }

    /**
     * After each test, close the client to free resources
     */
    @After
    public void tearDownWebClient() {
        client.close();
    }

}
