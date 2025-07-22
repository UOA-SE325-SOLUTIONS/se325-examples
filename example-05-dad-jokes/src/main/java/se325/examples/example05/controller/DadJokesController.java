package se325.examples.example05.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se325.examples.example05.model.DadJoke;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A Spring Boot REST controller for managing and serving dad jokes.
 * Provides endpoints to retrieve, add, and fetch dad jokes by ID.
 */
@RestController
@RequestMapping("/dad-jokes")
public class DadJokesController {

    private final AtomicLong counter = new AtomicLong();

    private final List<DadJoke> dadJokes;

    public DadJokesController() {
        this.dadJokes = new ArrayList<>(List.of(
                new DadJoke(counter.incrementAndGet(), "Why don't skeletons fight each other? They don't have the guts."),
                new DadJoke(counter.incrementAndGet(), "What do you call cheese that isn't yours? Nacho cheese."),
                new DadJoke(counter.incrementAndGet(), "How does a penguin build its house? Igloos it together."),
                new DadJoke(counter.incrementAndGet(), "What do you call fake spaghetti? An impasta."),
                new DadJoke(counter.incrementAndGet(), "What do you call a factory that makes okay products? A satisfactory."),
                new DadJoke(counter.incrementAndGet(), "Why did the scarecrow win an award? Because he was outstanding in his field."),
                new DadJoke(counter.incrementAndGet(), "Why don’t eggs tell jokes? They’d crack each other up."),
                new DadJoke(counter.incrementAndGet(), "Why couldn’t the bicycle stand up by itself? It was two tired."),
                new DadJoke(counter.incrementAndGet(), "What do you call a fish with no eyes? Fsh."),
                new DadJoke(counter.incrementAndGet(), "I would tell you a construction joke, but I’m still working on it.")
        ));
    }

    /**
     * Retrieves all dad jokes, or a subset matching the given searchText.
     *
     * @param searchText The text to search for in dad jokes. If null or blank, will be ignored.
     * @return A {@link List} of dad jokes.
     */
    @GetMapping
    public List<DadJoke> getAllDadJokes(@RequestParam(value = "search", required = false) String searchText) {
        if (searchText == null || searchText.isBlank()) return this.dadJokes;

        return this.dadJokes.stream()
                .filter(joke -> joke.getText().toLowerCase().contains(searchText.toLowerCase()))
                .toList();
    }

    /**
     * Retrieves a random dad joke.
     *
     * @return A random {@link DadJoke}.
     */
    @GetMapping("/random")
    public DadJoke getRandomDadJoke() {
        if (this.dadJokes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No dad jokes available");
        }
        int randomIndex = (int) (Math.random() * this.dadJokes.size());
        return this.dadJokes.get(randomIndex);
    }

    /**
     * Retrieves a dad joke by its ID.
     *
     * @param id The ID of the dad joke to retrieve.
     * @return The {@link DadJoke} object with the specified ID.
     * @throws ResponseStatusException If no dad joke with the given ID is found, a 404 Not Found error is thrown.
     */
    @GetMapping("/{id}")
    public DadJoke getDadJokeById(@PathVariable("id") long id) {

        return this.dadJokes.stream()
                .filter(joke -> joke.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dad joke not found"));
    }

    /**
     * Adds a new dad joke to the list.
     *
     * @param newJoke The dad joke to add.
     * @return The added dad joke with its generated ID.
     */
    @PostMapping
    public ResponseEntity<DadJoke> addDadJoke(@RequestBody DadJoke newJoke) throws URISyntaxException {
        if (newJoke.getText() == null || newJoke.getText().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dad joke text cannot be blank");
        }
        newJoke.setId(counter.incrementAndGet());
        this.dadJokes.add(newJoke);
        return ResponseEntity.created(new URI("/dad-jokes/" + newJoke.getId())).body(newJoke);
    }
}
