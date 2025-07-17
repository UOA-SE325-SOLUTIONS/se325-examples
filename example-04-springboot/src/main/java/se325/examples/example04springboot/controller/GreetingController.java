package se325.examples.example04springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se325.examples.example04springboot.model.Greeting;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/greetings")
public class GreetingController {

    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/hello")
    public Greeting getHelloGreeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), "Hello, " + name + "!");
    }
}
