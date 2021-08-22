package se325.example16.parolee.services;

import se325.example16.parolee.services.websockets.ParoleeMovementSubscriptionManager;

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
        singletons.add(ParoleViolationSubscriptionManager.instance());
        singletons.add(ParoleeMovementSubscriptionManager.instance());

        classes.add(ParoleeResource.class);
        classes.add(TestResource.class);
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
