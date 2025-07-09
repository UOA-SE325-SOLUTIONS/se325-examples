package se325.example05.helloworld.server;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/services")
public class HelloApplication extends Application {

    private final Set<Object> singletons = new HashSet<>();

    public HelloApplication() {
        singletons.add(new GreetingsResource());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
