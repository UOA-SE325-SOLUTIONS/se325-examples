package se325.example03.clients;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class HelloWorldClient {

    public static void main(String[] args) {
        Client httpClient = ClientBuilder.newClient();
        Response response = httpClient.target("http://localhost:8080/example_03_servlets_war_exploded/hello").request().get();
        String message = response.readEntity(String.class);
        System.out.println("Status: " + response.getStatus());
        System.out.println("Message: " + message);
        httpClient.close();

    }

}