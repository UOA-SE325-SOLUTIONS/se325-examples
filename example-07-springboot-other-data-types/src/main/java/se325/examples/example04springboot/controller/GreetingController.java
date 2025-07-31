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

    /**
     * This greeting endpoint will return a JSON object with a greeting in its
     * "message" property, and a unique id.
     *
     * @param name the name of the person to greet. Defaults to "World".
     * @return a {@link Greeting} object, as JSON.
     */
    @GetMapping(value = "/hello", produces = "application/json")
    public Greeting getHelloGreetingJson(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), "Hello, " + name + "!");
    }

    /**
     * This greeting endpoint will return a plaintext greeting.
     *
     * @param name the name of the person to greet. Defaults to "World".
     * @return a String to return to the client as plaintext.
     */
    @GetMapping(value = "/hello", produces = "text/plain")
    public String getHelloGreetingText(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }
}
