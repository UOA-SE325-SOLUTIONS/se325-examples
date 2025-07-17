package se325.examples.example04springboot.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import se325.examples.example04springboot.model.Greeting;

public class GreetingControllerUnitTest {

    GreetingController controller = new GreetingController();

    @Test
    public void testHelloGreetingWithDefaultName() {
        Greeting greeting = controller.getHelloGreeting("World");
        assertEquals("Hello, World!", greeting.getMessage());
    }

}