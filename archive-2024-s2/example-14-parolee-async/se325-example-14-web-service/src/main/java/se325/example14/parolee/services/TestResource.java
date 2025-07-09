package se325.example14.parolee.services;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

@Path("/parolees-test")
public class TestResource {

    @PUT
    @Path("/reset-database")
    public void reset() {
        PersistenceManager.instance().reset();
    }
}
