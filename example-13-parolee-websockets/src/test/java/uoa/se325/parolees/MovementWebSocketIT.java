package uoa.se325.parolees;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import uoa.se325.parolees.dto.ParoleeDTO;
import uoa.se325.parolees.model.*;
import uoa.se325.parolees.repository.ParoleeRepository;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This integration test looks a bit different than the usual ones.
 * <p>
 * This time we are going to be spinning up our app on an actual host (localhost) and port,
 * so we can test the WebSocket functionality. We aren't using MockMVC this time.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovementWebSocketIT {

    // This will be set to the port our app is listening on.
    @LocalServerPort
    private int port;

    // Used to add test data to the database
    @Autowired
    private ParoleeRepository repo;

    // Will store the PK of our parolee for testing
    private long nasserId;

    // These three are our STOMP / WebSocket client.
    private static WebSocketStompClient stompClient;
    private StompSession stompSession;
    private final BlockingQueue<ParoleeDTO> receivedMessages = new LinkedBlockingQueue<>();

    /**
     * Creates our STOMP client. We only need to do this once, before we run any tests.
     */
    @BeforeAll
    public static void createStompClient() {
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))
        ));

        // Allow the client to parse JSON, including Java date libraries.
        // This functionality isn't enabled by default for this client.
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(mapper);
        stompClient.setMessageConverter(converter);
    }

    /**
     * Creates test data in the database before each test
     */
    @BeforeEach
    public void createTestData() {
        // Create a parolee
        repo.deleteAll();
        Address nassersAddress = new Address("67", "Drayton Gardens", "Oraeki", "Auckland", "1071");
        Parolee nasser = new Parolee(
                "Giacaman",
                "Nasser",
                Gender.MALE,
                LocalDate.of(1980, 10, 19),
                nassersAddress);

        repo.save(nasser);
        this.nasserId = nasser.getId();
    }

    /**
     * Creates a new STOMP / WebSocket session for each test.
     */
    @BeforeEach
    public void createStompSession() throws Exception {
        // Clear messages
        receivedMessages.clear();

        // Connect to our server
        // The URL path /ws is coming from WebSocketConfig line 21
        var futureSession = stompClient.connectAsync(
                "ws://localhost:" + port + "/ws",
                new StompSessionHandlerAdapter() {
                    @Override
                    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                        System.err.println("WebSocket error: " + exception.getMessage());
                        exception.printStackTrace();
                    }

                    @Override
                    public void handleTransportError(StompSession session, Throwable exception) {
                        System.err.println("Transport error: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                }

        );

        stompSession = futureSession.join();

        // Subscribe to /topic/movements; add any received messages to our
        // receivedMessages queue to examine later.
        stompSession.subscribe("/topic/movements", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ParoleeDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received WebSocket message: " + payload);
                receivedMessages.add((ParoleeDTO) payload);
            }
        });
    }

    /**
     * Disconnect from the STOMP session after each test.
     */
    @AfterEach
    public void tearDown() {
        stompSession.disconnect();
    }

    @Test
    public void testMovementNotification() throws Exception {

        // POST a new movement for Nasser to our REST endpoint
        GeoPosition position = new GeoPosition(-36.852617, 174.769525);
        Movement movement = new Movement(LocalDateTime.now(), position);

        TestRestTemplate restTemplate = new TestRestTemplate();
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/parolees/" + nasserId + "/movements",
                movement,
                Void.class
        );

        // Make sure the POST succeeded
        assertEquals(204, response.getStatusCode().value());

        // This should have caused us to receive a broadcast Movement from our server.
        // Check it has the right info; timeout after 5 seconds.
        ParoleeDTO received = receivedMessages.poll(5, TimeUnit.SECONDS);
        assertNotNull(received);
        assertEquals("Nasser", received.getFirstName());
        assertEquals(movement, received.getLastKnownPosition());
    }
}

