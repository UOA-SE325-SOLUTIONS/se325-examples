package se325.example13.parolee.services;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Application subclass for the Parolee Web service.
 */
@ApplicationPath("/services")
public class ParoleeApplication extends Application {
    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> classes = new HashSet<>();

    public ParoleeApplication() {
        singletons.add(PersistenceManager.instance());
        classes.add(ParoleeResource.class);
        classes.add(TestResource.class);

//        new TestResource().reloadDatabase();
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
