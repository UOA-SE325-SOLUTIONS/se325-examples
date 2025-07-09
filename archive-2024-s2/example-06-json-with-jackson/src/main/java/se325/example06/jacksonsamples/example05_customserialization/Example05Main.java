package se325.example06.jacksonsamples.example05_customserialization;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

public class Example05Main {

    public static void main(String[] args) throws IOException {

        Movie movie = new Movie("The Neverending Story", LocalDate.of(1984, 04, 06));

        ObjectMapper mapper = new ObjectMapper();

        String movieJson = mapper.writeValueAsString(movie);
        System.out.println("Movie json: " + movieJson);

        Movie deserializedMovie = mapper.readValue(movieJson, Movie.class);
        System.out.println("Movie deserialized!");

        Student alice = new Student("Alice", 19);
        Student bob = new Student("Bob", 20);

        Course se325 = new Course("SOFTENG 325");

        University uni = new University();

        uni.getEnrollments().put(se325, Arrays.asList(alice, bob));

        String uniJson = mapper.writeValueAsString(uni);
        System.out.println("Uni json: " + uniJson);

        University deserializedUni = mapper.readValue(uniJson, University.class);
        System.out.println("Uni deserialized!");
    }

}
