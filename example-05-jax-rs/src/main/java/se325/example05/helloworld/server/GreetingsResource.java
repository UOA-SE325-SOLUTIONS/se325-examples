package se325.example05.helloworld.server;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/greetings")
public class GreetingsResource {

    @GET
    @Path("hello")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sayHello(@DefaultValue("Human") @QueryParam("name") String name) {

        String json = "{ \"greeting\": \"Hello, " + name + "!\" }";
        return Response.ok(json).build();
    }

}