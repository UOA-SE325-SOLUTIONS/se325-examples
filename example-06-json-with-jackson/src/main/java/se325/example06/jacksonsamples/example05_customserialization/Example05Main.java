package se325.example06.jacksonsamples.example05_customserialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

public class Example05Main {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Add support for Java8 data / time classes
        mapper.registerModule(new JavaTimeModule());
        
        // Demonstrate serialization of Movie class with a Java8 Date / Time class property
        Movie movie = new Movie("The Neverending Story", LocalDate.of(1984, 04, 06));
        String movieJson = mapper.writeValueAsString(movie);
        System.out.println("Movie json: " + movieJson);
        Movie deserializedMovie = mapper.readValue(movieJson, Movie.class);
        System.out.println("Movie deserialized!");

        // Demonstrate serialization of University class with a custom Map key
        Student alice = new Student("Alice", 19);
        Student bob = new Student("Bob", 20);
        Course se325 = new Course("SOFTENG 325");
        University uni = new University();
        uni.getEnrollments().put(se325, Arrays.asList(alice, bob));
        String uniJson = mapper.writeValueAsString(uni);
        System.out.println("Uni json: " + uniJson);
        University deserializedUni = mapper.readValue(uniJson, University.class);
        System.out.println("Uni deserialized!");
        
        // Demonstrate serialization of Pokemon class with a non-serializable Image property.
        // Use custom serializers / deserializers for this.
        BufferedImage dragoniteImage = ImageIO.read(
                Example05Main.class.getClassLoader().getResourceAsStream("Dragonite-Small.png"));
        System.out.println("Image successfully loaded: " + (dragoniteImage != null));
        Pokemon dragonite = new Pokemon(149, "Dragonite", dragoniteImage);
        System.out.println("Pokemon instance created: " + dragonite.getName());
        String pokemonJson = mapper.writeValueAsString(dragonite);
        System.out.println("Pokemon json: " + pokemonJson);
        Pokemon deserializedPokemon = mapper.readValue(pokemonJson, Pokemon.class);
        System.out.println("Pokemon deserialized!");
    }

}
