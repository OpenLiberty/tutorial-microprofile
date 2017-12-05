package application.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.json.JsonObject;
import javax.json.Json;

@Path("/")
public class LibertyRestEndpoint {   
    @GET
    @Path("/shipList")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject restTest2() {
        //return "This is a test message to test the back end microservice";
        JsonObject value = Json.createObjectBuilder()
        .add("shipName", "Titanic")
        .add("shipType", "Spaceship")
        .build();
        return value;
    }

}