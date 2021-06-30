package se325.example06.jacksonsamples.example07_polymorphism;

public class Dog extends Animal {

    public Dog() {
        this(null);
    }

    public Dog(String name) {
        super("Dog", name);
    }

    @Override
    public void sayHello() {
        System.out.println(name + " says woof!");
    }
}
