package se325.example06.jacksonsamples.example01_basic;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Example01Main {

    public static void main(String[] args) throws IOException {

        Book book = new Book("The Neverending Story", Genre.Fantasy);

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(book);
        System.out.println("Book json: " + json);

        Book deserialized = mapper.readValue(json, Book.class);

    }

}
