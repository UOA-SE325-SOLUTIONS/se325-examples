package se325.asyncchat.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.asyncchat.domain.Message;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Path("/chat")
public class ChatResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatResource.class);

    private final List<AsyncResponse> subs = new Vector<>();

    /**
     * Subscribes to be notified when the next message is received.
     */
    @GET
    @Path("/sub")
    @Produces(MediaType.APPLICATION_JSON)
    public void subscribeToMessage(@Suspended AsyncResponse sub) {
        synchronized (subs) {
            subs.add(sub);
        }
    }

    /**
     * POSTs a message, which will be pushed back to all subscribers.
     *
     * @param message the message to POST.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postMessage(Message message) {

        LOGGER.warn(message.toString());

        List<AsyncResponse> currentSubs;
        synchronized (subs) {
            currentSubs = new ArrayList<>(subs);
        }

        currentSubs.parallelStream().forEach(sub -> {
            sub.resume(message);
            synchronized (subs) {
                subs.remove(sub);
            }
        });

        return Response.ok().build();

    }

}
