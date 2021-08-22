package se325.example16.parolee.services;


import org.junit.Assert;
import org.junit.Test;
import se325.example16.parolee.common.Gender;
import se325.example16.parolee.common.Offence;
import se325.example16.parolee.dto.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class ParoleeWebServiceIT extends BaseIntegrationTests {

    /**
     * Tests that the Web service can create a new Parolee.
     */
    @Test
    public void testAddParolee() {
        AddressDTO homeAddress = new AddressDTO("34", "Appleby Road", "Remuera",
                "Auckland", "1070");
        ParoleeDTO zoran = new ParoleeDTO("Salcic", "Zoran", Gender.MALE,
                LocalDate.of(1958, 5, 17), homeAddress);

        Response response = client
                .target(WEB_SERVICE_URI).request()
                .post(Entity.json(zoran));

        if (response.getStatus() != 201) {
            fail("Failed to create new Parolee");
        }

        String location = response.getLocation().toString();
        response.close();

        // Query the Web service for the new Parolee.
        ParoleeDTO zoranFromService = client.target(location).request()
                .accept(MediaType.APPLICATION_JSON).get(ParoleeDTO.class);

        // The original local Parolee object (zoran) should have a value equal
        // to that of the Parolee object representing Zoran that is later
        // queried from the Web service. The only exception is the value
        // returned by getId(), because the Web service assigns this when it
        // creates a Parolee.
        assertEquals(zoran.getLastName(), zoranFromService.getLastName());
        assertEquals(zoran.getFirstName(), zoranFromService.getFirstName());
        assertEquals(zoran.getGender(), zoranFromService.getGender());
        assertEquals(zoran.getDateOfBirth(), zoranFromService.getDateOfBirth());
        assertEquals(zoran.getHomeAddress(), zoranFromService.getHomeAddress());
        assertNull(zoranFromService.getLastKnownPosition());
    }

    /**
     * Tests that the Web serves can process requests to record new Parolee movements.
     */
    @Test
    public void testAddParoleeMovement() {
        LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.NOON);
        MovementDTO newLocation = new MovementDTO(now, new GeoPositionDTO(
                -36.848238, 174.762212));

        Response response = client
                .target(WEB_SERVICE_URI + "/1/movements")
                .request().post(Entity.json(newLocation));
        if (response.getStatus() != 204) {
            fail("Failed to create new Movement");
        }
        response.close();

        // Query the Web service for the Parolee whose location has been
        // updated.
        ParoleeDTO oliver = client
                .target(WEB_SERVICE_URI + "/1").request()
                .accept(MediaType.APPLICATION_JSON).get(ParoleeDTO.class);

        // Check that the Parolee's location was updated.
        Assert.assertEquals(newLocation, oliver.getLastKnownPosition());
    }

    /**
     * Tests that the Web service can process Parolee update requests.
     */
    @Test
    public void testUpdateParolee() {
        final String targetUri = WEB_SERVICE_URI + "/2";

        // Query a Parolee (Catherine) from the Web service.
        ParoleeDTO catherine = client.target(targetUri).request()
                .accept(MediaType.APPLICATION_JSON).get(ParoleeDTO.class);

        AddressDTO originalAddress = catherine.getHomeAddress();

        // Update some of Catherine's details.
        AddressDTO newAddress = new AddressDTO("40", "Clifton Road", "Herne Bay",
                "Auckland", "1022");
        catherine.setHomeAddress(newAddress);

        Response response = client.target(targetUri).request()
                .put(Entity.json(catherine));

        if (response.getStatus() != 204) {
            fail("Failed to update Parolee");
        }
        response.close();

        // Requery Parolee from the Web service.
        ParoleeDTO updatedCatherine = client.target(targetUri).request()
                .accept(MediaType.APPLICATION_JSON).get(ParoleeDTO.class);

        // Parolee's home address should have changed.
        assertNotEquals(originalAddress, updatedCatherine.getHomeAddress());
        assertEquals(newAddress, updatedCatherine.getHomeAddress());
    }

    /**
     * Tests that the Web service can add disassociates to a Parolee.
     */
    @Test
    public void testUpdateDisassociates() {

        // Query Catherines's disassociates.
        List<ParoleeDTO> disassociates = client
                .target(WEB_SERVICE_URI + "/2/disassociates")
                .request().accept(MediaType.APPLICATION_JSON)
                .get(new GenericType<>() {
                });

        // Catherine should not yet have any recorded disassociates.
        assertTrue(disassociates.isEmpty());

        // Request that Nasser is added as a dissassociate to Catherine.
        Set<Long> newDisassociates = new HashSet<>();
        newDisassociates.add(3L);
        GenericEntity<Set<Long>> entity = new GenericEntity<>(newDisassociates) {
        };

        Response response = client
                .target(WEB_SERVICE_URI + "/2/disassociates")
                .request().put(Entity.json(entity));

        if (response.getStatus() != 204) {
            fail("Failed to update Parolee");
        }
        response.close();

        // Requery Catherine's dissassociates. The GET request is expected to
        // return a List<ParoleeDTO> object; since this is a parameterized type, a
        // GenericType wrapper is required so that the data can be unmarshalled.
        List<ParoleeDTO> updatedDisassociates = client
                .target(WEB_SERVICE_URI + "/2/disassociates")
                .request().accept(MediaType.APPLICATION_JSON)
                .get(new GenericType<>() {
                });

        // The Set of Parolees returned in response to the request for
        // Catherine's dissassociates should contain one object with the same
        // state (value) as the Parolee instance representing Nasser.
        assertEquals(1, updatedDisassociates.size());
        assertEquals("Nasser", updatedDisassociates.get(0).getFirstName());

    }

    /**
     * Tests that we can update a parolee's convictions
     */
    @Test
    public void testUpdateConvictions() {
        final String targetUri = WEB_SERVICE_URI + "/1/convictions";

        Set<ConvictionDTO> convictionsForOliver = client.target(targetUri).request()
                .accept(MediaType.APPLICATION_JSON).get(new GenericType<>() {
                });

        assertEquals(1, convictionsForOliver.size());
        assertEquals("Crime of passion", convictionsForOliver.iterator().next().getDescription());

        // Amend the criminal profile.
        convictionsForOliver.add(new ConvictionDTO(
                LocalDate.now(), "Shoplifting", Offence.THEFT));

        // Send a Web service request to update the profile.
        Response response = client.target(targetUri).request()
                .put(Entity.json(convictionsForOliver));

        if (response.getStatus() != 204) {
            fail("Failed to update CriminalProfile");
        }
        response.close();

        // Requery Oliver's criminal profile.
        Set<ConvictionDTO> reQueriedConvictions = client.target(targetUri).request()
                .accept(MediaType.APPLICATION_JSON).get(new GenericType<>() {
                });

        // The locally updated copy of Oliver's CriminalProfile should have
        // the same value as the updated profile obtained from the Web service.
        assertEquals(convictionsForOliver, reQueriedConvictions);
    }

    /**
     * Tests that the Web service can handle requests to query a particular Parolee.
     */
    @Test
    public void testRetrieveParolee() {
        ParoleeDTO parolee = client
                .target(WEB_SERVICE_URI + "/1").request()
                .accept(MediaType.APPLICATION_JSON).get(ParoleeDTO.class);

        assertEquals(1, (long) parolee.getId());
        assertEquals("Sinnen", parolee.getLastName());
    }

    /**
     * Tests that the Web service processes requests for all Parolees.
     */
    @Test
    public void testRetrieveAllParolees() {
        List<ParoleeDTO> parolees = client
                .target(WEB_SERVICE_URI).request()
                .accept(MediaType.APPLICATION_JSON)
                .get(new GenericType<>() {
                });
        assertEquals(3, parolees.size());
        assertEquals(1, (long) parolees.get(0).getId());
        assertEquals(2, (long) parolees.get(1).getId());
        assertEquals(3, (long) parolees.get(2).getId());
    }

    /**
     * Tests that the Web service processes requests for Parolees using header links for HATEOAS.
     */
    @Test
    public void testRetrieveParolees_HATEOAS() {
        // Make a request for Parolees, page = 0 and page size = 2. Should return the first two
        // parolees, along with a "next" header and no "prev" header.
        Response response = client
                .target(WEB_SERVICE_URI + "?page=0&size=2").request().get();

        // Extract links and entity data from the response.
        Link previous = response.getLink("prev");
        Link next = response.getLink("next");
        List<ParoleeDTO> parolees = response.readEntity(new GenericType<>() {
        });
        response.close();

        // The Web service should respond with a list containing only the first two Parolees.
        assertEquals(2, parolees.size());
        assertEquals(1, (long) parolees.get(0).getId());
        assertEquals(2, (long) parolees.get(1).getId());

        // Having requested the only the first page, the Web service should respond with a Next link,
        // but not a previous Link.
        assertNull(previous);
        assertNotNull(next);
        assertEquals("<" + WEB_SERVICE_URI + "?page=1&size=2>; rel=\"next\"", next.toString());

        // Invoke next link and extract response data.
        response = client.target(next).request().get();
        previous = response.getLink("prev");
        next = response.getLink("next");
        parolees = response.readEntity(new GenericType<>() {
        });
        response.close();

        // The third and final Parolee should be returned. The "prev" link should be a link for page 0, while the
        // "next" link should be null as there are no more parolees.
        assertEquals(1, parolees.size());
        assertEquals(3, (long) parolees.get(0).getId());
        assertNotNull(previous);
        assertEquals("<" + WEB_SERVICE_URI + "?page=0&size=2>; rel=\"prev\"", previous.toString());
        assertNull(next);
    }

    /**
     * Tests that the Web service can process requests for a particular Parolee's movements.
     */
    @Test
    public void testRetrieveParoleeMovements() {
        List<MovementDTO> movementsForOliver = client
                .target(WEB_SERVICE_URI + "/1/movements")
                .request().accept(MediaType.APPLICATION_JSON)
                .get(new GenericType<>() {
                });

        // Oliver has 3 recorded movements.
        assertEquals(3, movementsForOliver.size());

        // Make sure they're in timestamp order, latest first.
        assertTrue(movementsForOliver.get(0).getTimestamp().isAfter(movementsForOliver.get(1).getTimestamp()));
        assertTrue(movementsForOliver.get(1).getTimestamp().isAfter(movementsForOliver.get(2).getTimestamp()));
    }

    /**
     * Tests that we can subscribe to be notified of parole violations, and that we get notified successfully when such
     * a violation occurs.
     */
    @Test
    public void testSubscribeToParoleViolation() throws ExecutionException, InterruptedException, TimeoutException {

        // Oliver (ID 1) has a curfew from 7pm - 7am, at position -36.865520, 174.859520

        // Subscribe
        Future<Response> future = client.target(WEB_SERVICE_URI + "/1/subscribe-to-violations").request().async().post(null);

        // Oliver is not currently violating parole, so we shouldn't be notified just yet. Wait a second and see.
        try {
            future.get(1, TimeUnit.SECONDS);
            fail("Shouldn't have been successful as we're not violating parole yet");
        } catch (TimeoutException e) {

            // Oliver moves to a wine shop at 9pm - a definite no-no!
            MovementDTO movement = new MovementDTO(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 0)),
                    new GeoPositionDTO(-36.86555779774085, 174.85125285194638)
            );
            try (Response response = client.target(WEB_SERVICE_URI + "/1/movements")
                    .request().post(Entity.json(movement))) {
                assertEquals(204, response.getStatus());
            }

            // Now this should succeed. If we don't get notified within a few seconds, fail.
            // Otherwise, make sure the notification contains the correct info.
            try (Response notification = future.get(5, TimeUnit.SECONDS)) {
                assertEquals(200, notification.getStatus());
                ParoleViolationDTO violation = notification.readEntity(ParoleViolationDTO.class);
                assertEquals(1L, violation.getParoleeId());
                assertEquals(movement.getPosition(), violation.getLocation());
            }

        }
    }
}
