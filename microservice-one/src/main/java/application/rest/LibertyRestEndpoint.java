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
    public String hello() {
        return "Hello from the REST endpoint! I am alive!";
    } 
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject restTest1() {
        //return "This is a test message to test the back end microservice";
        JsonObject value = Json.createObjectBuilder()
        .add("firstName", "Jamie")
        .add("lastName", "Coleman")
        .add("age", 25)
        .add("address", Json.createObjectBuilder()
            .add("streetAddress", "1 Hurlsey Park")
            .add("city", "Winchester")
            .add("County", "Hampshire")
            .add("postalCode", "SO17 1PR"))
        .add("phoneNumber", Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                .add("type", "home")
                .add("number", "212 555-1234"))
            .add(Json.createObjectBuilder()
                .add("type", "fax")
                .add("number", "646 555-4567")))
        .build();
        return value;
    }
    @GET
    @Path("/systemprops")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getSystemInfo() {
        JsonObject value = Json.createObjectBuilder()
        .add("os", System.getProperty("os.name"))
        .add("osVersion", System.getProperty("os.version"))
        .add("osLanguage", System.getProperty("user.language"))
        .add("user", System.getProperty("user.name"))
        .add("userHome", System.getProperty("user.home"))
        .build();
        return value;
    }
}