package se325.example06.jacksonsamples.example02_customproperties;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Person {

    private String name;
    private int age;
    private String unreasonablePropertyNameOfDoom;
    private String ignoreMe;

    public Person() {
    }

    public Person(String name, int age, String unreasonablePropertyNameOfDoom) {
        this.name = name;
        this.age = age;
        this.unreasonablePropertyNameOfDoom = unreasonablePropertyNameOfDoom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @JsonGetter("reasonablePropName")
    public String getUnreasonablePropertyNameOfDoom() {
        return unreasonablePropertyNameOfDoom;
    }

    @JsonSetter("reasonablePropName")
    public void setUnreasonablePropertyNameOfDoom(String unreasonablePropertyNameOfDoom) {
        this.unreasonablePropertyNameOfDoom = unreasonablePropertyNameOfDoom;
    }

    @JsonIgnore
    public String getIgnoreMe() {
        return ignoreMe;
    }

    public void setIgnoreMe(String ignoreMe) {
        this.ignoreMe = ignoreMe;
    }
}
