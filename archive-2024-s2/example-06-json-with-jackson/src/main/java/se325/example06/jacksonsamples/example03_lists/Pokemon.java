package se325.example06.jacksonsamples.example03_lists;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Pokemon {

    private String name;

    private List<Type> types = new LinkedList<>();

    public Pokemon(){ }

    public Pokemon(String name) {
        this.name = name;
    }

    public Pokemon(String name, Type... types) {
        this(name);
        this.types.addAll(Arrays.asList(types));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }
}