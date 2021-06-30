package se325.example06.jacksonsamples.example03_lists;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Example03Main {

    public static void main(String[] args) throws IOException {

        Type fireType = new Type("Fire");
        Type flyingType = new Type("Flying");

        Pokemon charizard = new Pokemon("Charizard", fireType, flyingType);
        System.out.println("Charizard types list class: " + charizard.getTypes().getClass().getName());


        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(charizard);
        System.out.println("Charizard json: " + json);

        Pokemon deserialized = mapper.readValue(json, Pokemon.class);
        System.out.println("Deserialized types list class: " + deserialized.getTypes().getClass().getName());

    }

}