package se325.lab03.parolee.services;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * Application subclass for the Parolee Web service.
 */
@ApplicationPath("/services")
public class ParoleeApplication extends Application {
    private Set<Object> singletons = new HashSet<>();

    public ParoleeApplication() {
        // Register the ParoleeResource singleton to handle HTTP requests.
        ParoleeResource resource = new ParoleeResource();
        singletons.add(resource);
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
