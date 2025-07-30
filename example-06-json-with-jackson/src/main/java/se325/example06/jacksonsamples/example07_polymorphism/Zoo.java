package se325.example06.jacksonsamples.example07_polymorphism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Zoo {

    private List<Animal> animals = new ArrayList<>();

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    public void add(Animal animal) {
        this.animals.add(animal);
    }

    @Override
    public String toString() {
        return "Zoo [Animals: " + Arrays.toString(animals.toArray()) + "]";
    }
}
