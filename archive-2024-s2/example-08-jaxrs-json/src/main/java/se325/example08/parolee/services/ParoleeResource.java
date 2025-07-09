package se325.example08.parolee.services;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import se325.example08.parolee.domain.Parolee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to implement a simple REST Web service for managing parolees.
 * <p>
 * ParoleeResource implements a WEB service with the following interface:
 * <p>
 * - GET    <base-uri>/parolees/{id}
 * Retrieves a parolee based on their unique id. The format of the
 * returned data is JSON.
 * <p>
 * - POST   <base-uri>/parolees
 * Creates a new Parolee. The HTTP post message contains an JSON
 * representation of the parolee to be created.
 * <p>
 * - PUT    <base-uri>/parolees/{id}
 * Updates a parolee, identified by their id.The HTTP PUT message
 * contains an JSON document describing the new state of the parolee.
 * <p>
 * - DELETE <base-uri>/parolees/{id}
 * Deletes a parolee, identified by their unique id.
 * <p>
 * - DELETE <base-uri>/parolees
 * Deletes all parolees.
 */
@Path("/parolees")
public class ParoleeResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParoleeResource.class);

    private Map<Long, Parolee> paroleeDB = new ConcurrentHashMap<>();
    private AtomicLong idCounter = new AtomicLong();

    /**
     * Creates a new Parolee.
     *
     * @param parolee the deserialized Parolee to be created
     * @return a Response object that includes the HTTP "Location" header,
     * whose value is the URI of the newly created resource. The HTTP
     * response code is 201. The JAX-RS run-time processes the Response
     * object when preparing the HTTP response message.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createParolee(Parolee parolee) {

        // Generate an ID for the new Parolee, and store it in memory.
        parolee.setId(idCounter.incrementAndGet());
        paroleeDB.put(parolee.getId(), parolee);

        LOGGER.debug("Created parolee with id: " + parolee.getId());

        return Response
                .created(URI.create("/parolees/" + parolee.getId()))
                .build();
    }

    /**
     * Attempts to retrieve a particular Parolee based on their unique id. If
     * the required Parolee is found, this method returns a 200 response along
     * with an JSON representation of the Parolee. In other cases, this method
     * returns a 404 response.
     *
     * @param id the unique id of the Parolee to be returned.
     * @return a StreamingOutput object that writes out the Parolee state in
     * JSON form.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Parolee retrieveParolee(@PathParam("id") long id) {
        LOGGER.info("Retrieving parolee with id: " + id);
        // Lookup the Parolee within the in-memory data structure.
        final Parolee parolee = paroleeDB.get(id);
        if (parolee == null) {
            // Return a HTTP 404 response if the specified Parolee isn't found.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return parolee;
    }

    /**
     * Attempts to update an existing Parolee. If the specified Parolee is
     * found it is updated, resulting in a HTTP 204 response being returned to
     * the consumer. In other cases, a 404 response is returned.
     *
     * @param id     the unique id of the Parolee to update.
     * @param update the Parolee to update
     */
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateParolee(@PathParam("id") long id, Parolee update) {
        Parolee current = paroleeDB.get(id);
        if (current == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Update the details of the Parolee to be updated.
        current.setFirstName(update.getFirstName());
        current.setLastName(update.getLastName());
        current.setGender(update.getGender());
        current.setDateOfBirth(update.getDateOfBirth());

        // Methods with no return type (i.e. void) will return HTTP 204 to the client.
    }

    /**
     * Attempts to delete an existing Parolee. If the specified Parolee isn't
     * found, a 404 response is returned to the consumer. In other cases, a 204
     * response is returned after deleting the Parolee.
     *
     * @param id the unique id of the Parolee to delete.
     */
    @DELETE
    @Path("{id}")
    public void deleteParolee(@PathParam("id") long id) {
        Parolee current = paroleeDB.get(id);
        if (current == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Remove the Parolee.
        paroleeDB.remove(id);
        LOGGER.info("Deleted parolee with ID: " + id);

        // Methods with no return type (i.e. void) will return HTTP 204 to the client.
    }

    /**
     * Deletes all Parolees. A 204 response is returned to the consumer.
     */
    @DELETE
    public void deleteAllParolees() {
        paroleeDB.clear();
        idCounter = new AtomicLong();
    }

}
