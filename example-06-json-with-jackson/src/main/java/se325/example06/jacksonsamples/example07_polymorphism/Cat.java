package se325.example06.jacksonsamples.example07_polymorphism;

public class Cat extends Animal {

    public Cat() {
        this(null);
    }

    public Cat(String name) {
        super("Cat", name);
    }

    @Override
    public void sayHello() {
        System.out.println(name + " says Meow!");
    }
}
