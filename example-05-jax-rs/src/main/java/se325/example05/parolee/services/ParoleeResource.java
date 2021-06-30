package se325.example05.parolee.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.example05.parolee.domain.Gender;
import se325.example05.parolee.domain.Parolee;

/**
 * Class to implement a simple REST Web service for managing parolees.
 * <p>
 * ParoleeResource implements a WEB service with the following interface:
 * <p>
 * - GET    <base-uri>/parolees/{id}
 * Retrieves a parolee based on their unique id. The format of the returned data is JSON.
 * <p>
 * - POST   <base-uri>/parolees
 * Creates a new Parolee. The HTTP post message contains a JSON representation of the parolee to be created.
 * <p>
 * - PUT    <base-uri>/parolees/{id}
 * Updates a parolee, identified by their id.The HTTP PUT message contains a JSON document describing the new state
 * of the parolee.
 * <p>
 * - DELETE <base-uri>/parolees/{id}
 * Deletes a parolee, identified by their unique id.
 * <p>
 * - DELETE <base-uri>/parolees
 * Deletes all parolees.
 */
@Path("/parolees")
public class ParoleeResource {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static Logger _logger = LoggerFactory.getLogger(ParoleeResource.class);

    private Map<Long, Parolee> _paroleeDB = new ConcurrentHashMap<>();
    private AtomicLong _idCounter = new AtomicLong();

    /**
     * Attempts to retrieve a particular Parolee based on their unique id. If the required Parolee is found, this method
     * returns a 200 response along with a JSON representation of the Parolee. In other cases, this method returns a
     * 404 response.
     *
     * @param id the unique id of the Parolee to be returned.
     * @return a StreamingOutput object that writes out the Parolee state in JSON form.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public StreamingOutput getParolee(@PathParam("id") long id) {
        _logger.info("Retrieving parolee with id: " + id);
        // Lookup the Parolee within the in-memory data structure.
        final Parolee parolee = _paroleeDB.get(id);
        if (parolee == null) {
            // Return a HTTP 404 response if the specified Parolee isn't found.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Return a StreamingOuput instance that the JAX-RS implementation will use to set the body of the
        // HTTP response message.
        return (outputStream) -> outputParolee(outputStream, parolee);
    }

    /**
     * Creates a new Parolee.
     *
     * @param is the InputStream that contains a JSON representation of the Parolee to be created.
     * @return a Response object that includes the HTTP "Location" header, whose value is the URI of the newly created
     * resource. The HTTP response code is 201. The JAX-RS run-time processes the Response object when preparing
     * the HTTP response message.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createParolee(InputStream is) {
        // Read an XML representation of a new Parolee. Note that with JAX-RS, any non-annotated parameter in a
        // Resource method is assumed to hold the HTTP request's message body.
        Parolee parolee = readParolee(is);

        // Generate an ID for the new Parolee, and store it in memory.
        parolee.setId(_idCounter.incrementAndGet());
        _paroleeDB.put(parolee.getId(), parolee);

        _logger.debug("Created parolee with id: " + parolee.getId());

        return Response.created(URI.create("/parolees/" + parolee.getId()))
                .build();
    }

    /**
     * Attempts to update an existing Parolee. If the specified Parolee is found it is updated, resulting in a
     * HTTP 204 response being returned to the consumer. In other cases, a 404 response is returned.
     *
     * @param id the unique id of the Parolee to update.
     * @param is the InputStream used to store a JSON representation of the new state for the Parolee.
     */
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateParolee(@PathParam("id") long id, InputStream is) {
        Parolee update = readParolee(is);
        Parolee current = _paroleeDB.get(id);
        if (current == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Update the details of the Parolee to be updated.
        current.setFirstName(update.getFirstName());
        current.setLastName(update.getLastName());
        current.setGender(update.getGender());
        current.setDateOfBirth(update.getDateOfBirth());
    }

    /**
     * Attempts to delete an existing Parolee. If the specified Parolee isn't found, a 404 response is returned to the
     * consumer. In other cases, a 204 response is returned after deleting the Parolee.
     *
     * @param id the unique id of the Parolee to delete.
     */
    @DELETE
    @Path("{id}")
    public void deleteParolee(@PathParam("id") long id) {
        Parolee current = _paroleeDB.get(id);
        if (current == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Remove the Parolee.
        _paroleeDB.remove(id);
        _logger.info("Deleted parolee with ID: " + id);
    }

    /**
     * Deletes all Parolees. A 204 response is returned to the consumer.
     */
    @DELETE
    public void deleteAllParolees() {
        _paroleeDB.clear();
        _idCounter = new AtomicLong();
    }


    /**
     * Helper method to generate a JSON representation of a particular Parolee.
     *
     * @param os      the OutputStream used to write out the JSON.
     * @param parolee the Parolee for which to generate a JSON representation.
     */
    protected void outputParolee(OutputStream os, Parolee parolee) {

        String dateOfBirth = parolee.getDateOfBirth().format(DATE_FORMATTER);

        PrintStream writer = new PrintStream(os);
        writer.println("{");
        writer.println("  \"id\": " + parolee.getId() + ",");
        writer.println("  \"firstName\": \"" + parolee.getFirstName() + "\",");
        writer.println("  \"lastName\": \"" + parolee.getLastName() + "\",");
        writer.println("  \"gender\": \"" + parolee.getGender() + "\",");
        writer.println("  \"dateOfBirth\": \"" + dateOfBirth + "\"");
        writer.println("}");
    }

    /**
     * Helper method to generate a JSON representation for a collection of Parolees.
     */
    protected void outputParolees(OutputStream os, List<Parolee> parolees) {
        PrintStream writer = new PrintStream(os);
        writer.println("[");
        for (int i = 0; i < parolees.size(); i++) {
            outputParolee(os, parolees.get(i));
            if (i < parolees.size() - 1) {
                writer.println(",");
            }
        }
        writer.println("]");
    }

    /**
     * Helper method to read a JSON representation of a Parolee, and return a corresponding Parolee object.
     *
     * @param is the InputStream containing an XML representation of the Parolee to create.
     * @return a new Parolee object.
     */
    protected Parolee readParolee(InputStream is) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(is);

            Parolee parolee = new Parolee();
            JsonNode idNode = node.get("id");
            parolee.setId(idNode == null ? 0 : idNode.asLong());
            parolee.setFirstName(node.get("firstName").asText());
            parolee.setLastName(node.get("lastName").asText());
            parolee.setGender(Gender.valueOf(node.get("gender").asText()));
            parolee.setDateOfBirth(LocalDate.parse(node.get("dateOfBirth").asText(), DATE_FORMATTER));

            return parolee;

        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

}