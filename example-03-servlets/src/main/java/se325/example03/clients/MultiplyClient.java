package se325.example03.clients;

import se325.util.Keyboard;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MultiplyClient {

    public static void main(String[] args) {

        int num1 = Integer.parseInt(Keyboard.prompt("Please enter first number:"));
        int num2 = Integer.parseInt(Keyboard.prompt("Please enter second number:"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(
                        "http://localhost:8080/my-app/basicMultiply?num1=" + num1 + "&num2=" + num2
                ))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(result -> System.out.println("Result: " + result))
                .join();

    }

}
