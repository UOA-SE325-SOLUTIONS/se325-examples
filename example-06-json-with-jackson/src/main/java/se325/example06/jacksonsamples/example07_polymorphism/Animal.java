package se325.example06.jacksonsamples.example07_polymorphism;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = Cat.class, name = "Cat"),
//        @JsonSubTypes.Type(value = Dog.class, name = "Dog")
//})
public abstract class Animal {

    protected final String speciesName;

    protected String name;

    public Animal(String speciesName) {
        this.speciesName = speciesName;
    }

    public Animal(String speciesName, String name) {
        this(speciesName);
        this.name = name;
    }

    @JsonIgnore
    public String getSpeciesName() {
        return speciesName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void sayHello();

    @Override
    public String toString() {
        return name + " the " + speciesName;
    }
}