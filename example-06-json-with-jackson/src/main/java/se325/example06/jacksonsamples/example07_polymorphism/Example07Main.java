package se325.example06.jacksonsamples.example07_polymorphism;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Example07Main {

    public static void main(String[] args) throws IOException {

        Zoo zoo = new Zoo();
        zoo.add(new Cat("Mufasa"));
        zoo.add(new Dog("Lassie"));

        ObjectMapper mapper = new ObjectMapper();

        String zooJson = mapper.writeValueAsString(zoo);
        System.out.println("Zoo json: " + zooJson);

        Zoo deserializedZoo = mapper.readValue(zooJson, Zoo.class);
        System.out.println("Zoo deserialized");
        for (Animal a : deserializedZoo.getAnimals()) {
            a.sayHello();
        }

    }

}
