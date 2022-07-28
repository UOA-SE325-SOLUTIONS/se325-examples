package se325.example05.helloworld.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class GreetingsClient {

    public static void main(String[] args) {

        Client client = ClientBuilder.newClient();
        Response response = client.target("http://localhost:8080/example_05_jax_rs_war_exploded/services/greetings/hello?name=Bob").request().get();
        String json = response.readEntity(String.class);
        System.out.println("Response JSON: " + json);
        client.close();

    }

}
