package se325.example06.jacksonsamples.example02_customproperties;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Example02Main {

    public static void main(String[] args) throws IOException {

        Person person = new Person("Bob", 42, "Stuff & things");

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(person);
        System.out.println("Person json: " + json);

        Person deserialized = mapper.readValue(json, Person.class);

    }

}
