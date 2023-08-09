package se325.example.bonus02;

import se325.example.bonus02.proxymagic.Injector;

import java.util.List;

public class Main {

    public static void main(String[] args) throws ReflectiveOperationException {
        AnnotatedDemoClass demo = new AnnotatedDemoClass();

        Injector.inject(demo);

        // Adding and removing things from the demo list should result in logging statements in the console, while
        // still functioning as a proper list.
        List<String> list = demo.getDemoList();
        list.add("Hello, world!");
        System.out.println("List size is " + list.size());
        System.out.println("List at position 0 is " + list.get(0));
        list.remove(0);
        System.out.println("List size is " + list.size());

        System.out.println();

        // Getting any of Dave's properties should return id 1, name "Dave", hobby "Stuff & Things".
        Person dave = demo.getDave();
        System.out.println(dave.toString());
    }
}
