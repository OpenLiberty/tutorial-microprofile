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
     * Instantiate a StatsManager locally which will not be in the CDI context
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
    @Path("/systemprops")
    @Timed
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
