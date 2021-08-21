package se325.asyncchat.services;

import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/services")
public class ChatApplication extends Application {

    private final Set<Object> singletons = new HashSet<>();

    public ChatApplication() {
        this.singletons.add(new ChatResource());

        LoggerFactory.getLogger(ChatApplication.class).warn("ChatApplication running!!");

    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
