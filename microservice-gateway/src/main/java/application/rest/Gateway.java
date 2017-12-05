package application.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.json.JsonObject;

import javax.inject.Inject;

@Path("/")
public class Gateway {
  //Server locations! This will be moved to enviroment variables at a later date!
  String microservice1 = "http://localhost:9090/LibertyMicroServiceOne-1.0";
  String microservice2 = "http://localhost:9091/LibertyMicroServiceTwo-1.0";

  @Inject
  public Proxy proxy;
  //Test method to get some random data back
  @GET
  @Path("/json")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject test() throws Exception{
    String endpoint = "/rest/json";
    return proxy.sendGetRequest(microservice1, endpoint);
  }

  @GET
  @Path("/systemprops")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject systemProps() throws Exception{
    String endpoint = "/rest/systemprops";
    return proxy.sendGetRequest(microservice1, endpoint);
  }

  @GET
  @Path("/shipList")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject listSpaceships() throws NullPointerException{
    String endpoint = "/rest/shipList";
    return proxy.sendGetRequest(microservice2, endpoint);
  }

  @GET
  @Path("/aliens/list")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject listAliens() throws Exception{
    String endpoint = "/rest/aliens/list";
    return proxy.sendGetRequest(microservice2, endpoint);
  }

  // This method may not yet work and requires more testing!
  @POST
  @Path("/aliens/new")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject addAliens() {
    String endpoint = "/rest/aliens/new";
    return proxy.sendPostRequest(microservice2, endpoint);
  }

  @GET
  @Path("/health1")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject getMicroserviceOneHealth() {
    String endpoint = "/health";
    return proxy.sendGetRequest("http://localhost:9090", endpoint);
  }

  @GET
  @Path("/health2")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject getMicroserviceTwoHealth() {
    String endpoint = "/health";
    return proxy.sendGetRequest("http://localhost:9091", endpoint);
  }

  @GET
  @Path("/metrics1")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject getMicroserviceOneMetrics() {
    String endpoint = "/metrics";
    return proxy.sendGetRequest("https://localhost:9490", endpoint);
  }
}