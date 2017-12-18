package application.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.json.JsonObject;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;

@Path("/")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class LibertyRestEndpoint {   


    private JaxrsManager jaxrsManager;
    //private JaxrsManager jaxrsManager;

    /**
     * Instantiate a JaxrsManager locally which will not be in the CDI context
     * 
     * @param jaxrsManager
     */
    @Inject
    private void setup (MetricRegistry registry) {
        //this.jaxrsManager = jaxrsManager;
        this.jaxrsManager = new JaxrsManager();
    }

    @GET
    @Path("/json")
    @Timed
    public JsonObject restTest1() {
        //return "This is a test message to test the back end microservice";
        return jaxrsManager.getTestData();
    }
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