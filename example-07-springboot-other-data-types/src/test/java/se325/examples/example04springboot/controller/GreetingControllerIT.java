package se325.examples.example04springboot.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class GreetingControllerIT {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testHelloGreetingWithDefaultName() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/greetings/hello")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Hello, World!")));
    }

    @Test
    public void testHelloGreetingWithGivenName() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/greetings/hello?name=Bob")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Hello, Bob!")));
    }
}
