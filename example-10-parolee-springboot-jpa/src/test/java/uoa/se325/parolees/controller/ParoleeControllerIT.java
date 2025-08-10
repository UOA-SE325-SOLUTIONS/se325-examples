package uoa.se325.parolees.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.MockMvc;
import uoa.se325.parolees.model.*;
import uoa.se325.parolees.repository.ParoleeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ParoleeControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ParoleeRepository repo;

    private long oliverId;
    private long catherineId;
    private long nasserId;

    /**
     * Setup test data in the database before each unit test runs.
     * <p>
     * With this, we don't need the @DirtiesContext above, since all application state is in the database.
     */
    @BeforeEach
    public void setUp() {
        repo.deleteAll();

        GeoPosition addressLocation = new GeoPosition(-36.865520, 174.859520);
        Address oliversAddress = new Address("15", "Bermuda road", "St Johns", "Auckland", "1071", addressLocation);
        Parolee oliver = new Parolee(
                "Sinnen",
                "Oliver",
                Gender.MALE,
                LocalDate.of(1970, 5, 26),
                oliversAddress);

        oliver.getConvictions().add(new Conviction(LocalDate.of(
                1994, 1, 19), "Crime of passion", Offence.MURDER));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earlierToday = now.minusHours(1);
        LocalDateTime yesterday = now.minusDays(1);
        GeoPosition position = new GeoPosition(-36.852617, 174.769525);

        oliver.addMovement(new Movement(yesterday, position));
        oliver.addMovement(new Movement(earlierToday, position));
        oliver.addMovement(new Movement(now, position));

        repo.save(oliver);
        oliverId = oliver.getId();

        Address catherinesAddress = new Address("22", "Tarawera Terrace", "St Heliers", "Auckland", "1071");
        Parolee catherine = new Parolee(
                "Watson",
                "Catherine",
                Gender.FEMALE,
                LocalDate.of(1970, 2, 9),
                catherinesAddress);

        repo.save(catherine);
        catherineId = catherine.getId();

        Address nassersAddress = new Address("67", "Drayton Gardens", "Oraeki", "Auckland", "1071");
        Parolee nasser = new Parolee(
                "Giacaman",
                "Nasser",
                Gender.MALE,
                LocalDate.of(1980, 10, 19),
                nassersAddress);

        repo.save(nasser);
        nasserId = nasser.getId();
    }

    @Test
    public void testContextLoads() {
    }

    @Test
    public void testCreateParolee() throws Exception {
        String newParoleeJson = """
                {
                    "lastName": "Salcic",
                    "firstName": "Zoran",
                    "gender": "MALE",
                    "dateOfBirth": "1958-05-17",
                    "homeAddress": {
                        "streetNumber": "34",
                        "streetName": "Appleby Road",
                        "suburb": "Remuera",
                        "city": "Auckland",
                        "zipCode": "1070"
                    }
                }
                """;

        var result = mvc.perform(post("/parolees")
                        .content(newParoleeJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lastName").value("Salcic"))
                .andExpect(jsonPath("$.homeAddress.suburb").value("Remuera"))
                .andReturn().getResponse();

        assertNotNull(result.getHeader("Location"));
        String paroleeId = result
                .getHeader("Location")
                .substring(result.getHeader("Location").lastIndexOf("/") + 1);

        // Check database
        var parolee = repo.findById(Long.parseLong(paroleeId)).orElse(null);
        assertNotNull(parolee);
        assertEquals("Salcic", parolee.getLastName());

    }


    @Test
    public void testAddParoleeMovement() throws Exception {
        String newMovementJson = """
                {
                    "timestamp": "2023-10-01T10:00:00",
                    "geoPosition": {
                        "latitude": -36.8509,
                        "longitude": 174.7645
                    }
                }
                """;

        mvc.perform(post("/parolees/{id}/movements", oliverId)
                        .content(newMovementJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify the new movement exists in the database
        var parolee = repo.findById(oliverId).orElse(null);
        assertNotNull(parolee);
        assertEquals(4, parolee.getMovements().size());
        Movement latestMovement = parolee.getMovements().get(parolee.getMovements().size() - 1);
        assertEquals(-36.8509, latestMovement.getGeoPosition().getLatitude());
        assertEquals(174.7645, latestMovement.getGeoPosition().getLongitude());
    }

    @Test
    public void testUpdateParoleeDetails() throws Exception {
        String updateParoleeJson = """
                {
                    "lastName": "UpdatedLastName",
                    "firstName": "UpdatedFirstName",
                    "dateOfBirth": "1985-06-15",
                    "homeAddress": {
                        "streetNumber": "10",
                        "streetName": "Updated Street",
                        "suburb": "Updated Suburb",
                        "city": "Auckland",
                        "zipCode": "9999"
                    }
                }
                """;

        mvc.perform(patch("/parolees/{id}", oliverId)
                        .content(updateParoleeJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify the changes in the database
        var updatedParolee = repo.findById(oliverId).orElse(null);
        assertNotNull(updatedParolee);
        assertEquals("UpdatedLastName", updatedParolee.getLastName());
        assertEquals("UpdatedFirstName", updatedParolee.getFirstName());
        // This one should not have updated.
        assertEquals("MALE", updatedParolee.getGender().toString());
        assertEquals("10", updatedParolee.getHomeAddress().getStreetNumber());
        assertEquals("Updated Street", updatedParolee.getHomeAddress().getStreetName());
        assertEquals("Updated Suburb", updatedParolee.getHomeAddress().getSuburb());
        assertEquals("Auckland", updatedParolee.getHomeAddress().getCity());
        assertEquals("9999", updatedParolee.getHomeAddress().getZipCode());
    }


    @Test
    @Transactional
    public void testSetDisassociates() throws Exception {
        String dissIds = "[" + nasserId + "]";

        mvc.perform(put("/parolees/{id}/disassociates", catherineId)
                        .content(dissIds)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify any disassociation-specific database changes if applicable
        var updatedParolee = repo.findById(catherineId).orElse(null);
        assertNotNull(updatedParolee);
        assertEquals(1, updatedParolee.getDisassociates().size());
        Parolee nasser = updatedParolee.getDisassociates().iterator().next();
        assertEquals("Nasser", nasser.getFirstName());
    }

    @Test
    @Transactional
    public void testSetConvictions() throws Exception {
        String newConvictionJson = """
                [
                    {
                        "date": "2021-12-15",
                        "description": "Theft",
                        "offence": "THEFT"
                    },
                    {
                        "date": "2022-07-08",
                        "description": "Assault",
                        "offence": "ASSAULT"
                    }
                ]
                """;

        mvc.perform(put("/parolees/{id}/convictions", oliverId)
                        .content(newConvictionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify the convictions have been updated in the database
        var updatedParolee = repo.findById(oliverId).orElse(null);
        assertNotNull(updatedParolee);

        assertEquals(2, updatedParolee.getConvictions().size());
        assertTrue(updatedParolee.getConvictions().containsAll(List.of(
                new Conviction(LocalDate.parse("2021-12-15"), "Theft", Offence.THEFT),
                new Conviction(LocalDate.parse("2022-07-08"), "Assault", Offence.ASSAULT)
        )));
    }

    @Test
    public void testGetParoleeById() throws Exception {
        // Perform a GET request for a specific parolee
        mvc.perform(get("/parolees/{id}", oliverId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Sinnen"))
                .andExpect(jsonPath("$.firstName").value("Oliver"))
                .andExpect(jsonPath("$.homeAddress.city").value("Auckland"));
    }


    @Test
    public void testGetAllParolees() throws Exception {
        // Perform a GET request to fetch all parolees
        mvc.perform(get("/parolees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)) // Assuming there are 3 parolees in the database
                .andExpect(jsonPath("$[0].lastName").value("Sinnen"))
                .andExpect(jsonPath("$[0].firstName").value("Oliver"))
                .andExpect(jsonPath("$[1].lastName").value("Watson"))
                .andExpect(jsonPath("$[1].firstName").value("Catherine"))
                .andExpect(jsonPath("$[2].lastName").value("Giacaman"))
                .andExpect(jsonPath("$[2].firstName").value("Nasser"));
    }


    @Test
    public void testGetAllParoleesWithPaginationFirstPage() throws Exception {
        // Perform a GET request with pagination parameters
        mvc.perform(get("/parolees")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Prev-Page"))
                .andExpect(header().exists("Next-Page"))
                .andExpect(header().string("Next-Page", "1"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2)) // Expecting 2 parolees in the response
                .andExpect(jsonPath("$[0].lastName").value("Sinnen"))
                .andExpect(jsonPath("$[0].firstName").value("Oliver"))
                .andExpect(jsonPath("$[1].lastName").value("Watson"))
                .andExpect(jsonPath("$[1].firstName").value("Catherine"));
    }

    @Test
    public void testGetAllParoleesWithPaginationSecondPage() throws Exception {
        // Perform a GET request with pagination parameters
        mvc.perform(get("/parolees")
                        .param("page", "1")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Prev-Page"))
                .andExpect(header().string("Prev-Page", "0"))
                .andExpect(header().doesNotExist("Next-Page"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].lastName").value("Giacaman"))
                .andExpect(jsonPath("$[0].firstName").value("Nasser"));
    }


    @Test
    public void testGetParoleeMovements() throws Exception {
        // Perform a GET request to fetch movements of a specific parolee
        mvc.perform(get("/parolees/{id}/movements", oliverId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].geoPosition.latitude").value(-36.852617))
                .andExpect(jsonPath("$[0].geoPosition.longitude").value(174.769525))
                .andExpect(jsonPath("$[0].timestamp").isNotEmpty())
                .andExpect(jsonPath("$[1].geoPosition.latitude").value(-36.852617))
                .andExpect(jsonPath("$[1].geoPosition.longitude").value(174.769525))
                .andExpect(jsonPath("$[1].timestamp").isNotEmpty())
                .andExpect(jsonPath("$[2].geoPosition.latitude").value(-36.852617))
                .andExpect(jsonPath("$[2].geoPosition.longitude").value(174.769525))
                .andExpect(jsonPath("$[2].timestamp").isNotEmpty());
    }
}
