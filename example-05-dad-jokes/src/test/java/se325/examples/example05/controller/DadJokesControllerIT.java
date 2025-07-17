package se325.examples.example05.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DadJokesControllerIT {

    @Autowired
    private MockMvc mvc;

    /**
     * Tests that the controller returns all dad jokes (there should be 10 of them)
     * <p>
     * TODO Modify this test to confirm the values of all 10 jokes, not just the first one.
     */
    @Test
    public void testGetAllDadJokes() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/dad-jokes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].text").value("Why don't skeletons fight each other? They don't have the guts."));
    }

    /**
     * Tests that searching for dad jokes returns the correct results when there is a single result
     */
    @Test
    public void testSearchDadJokes() throws Exception {
        String searchText = "skeletons";

        mvc.perform(MockMvcRequestBuilders
                        .get("/dad-jokes")
                        .param("search", searchText)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].text").value("Why don't skeletons fight each other? They don't have the guts."));
    }

    /**
     * Tests that searching for dad jokes returns the correct results when there are multiple matches
     */
    @Test
    public void testSearchDadJokesMultipleResults() throws Exception {
        String searchText = "what do you call";

        mvc.perform(MockMvcRequestBuilders
                        .get("/dad-jokes")
                        .param("search", searchText)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4));
    }

    /**
     * Tests that the controller returns correct dad joke by id
     */
    @Test
    public void testGetDadJokeById() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/dad-jokes/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Why don't skeletons fight each other? They don't have the guts."));
    }

    /**
     * Tests that the controller returns HTTP 404 for non-existing dad joke id
     */
    @Test
    public void testGetNonExistingDadJoke() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/dad-jokes/10000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests that a random dad joke can be obtained successfully.
     *
     */
    @Test
    public void testGetRandomDadJoke() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/dad-jokes/random")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").exists());
    }

    /**
     * Tests that a new dad joke can be added successfully
     */
    @Test
    public void testAddDadJoke() throws Exception {
        String newJoke = "{\"text\":\"Why do seagulls fly over the sea? Because if they flew over the bay they would be bagels.\"}";

        mvc.perform(MockMvcRequestBuilders
                        .post("/dad-jokes")
                        .content(newJoke)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Why do seagulls fly over the sea? Because if they flew over the bay they would be bagels."));
    }

    /**
     * Tests that adding a joke with blank text results in an HTTP 400 error
     */
    @Test
    public void testAddJokeWithBlankText() throws Exception {
        String blankJoke = "{\"text\":\"\"}";

        mvc.perform(MockMvcRequestBuilders
                        .post("/dad-jokes")
                        .content(blankJoke)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
