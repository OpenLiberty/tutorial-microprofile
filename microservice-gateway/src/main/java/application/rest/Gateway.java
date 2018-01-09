package application.rest;

import javax.ws.rs.GET;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.json.JsonObject;

import java.io.IOException;


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

  public JsonObject test() throws IOException{
    String endpoint = "/rest/json";
    return proxy.sendGetRequest(microservice1, endpoint);
  }

  @GET
  @Path("/systemprops")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject systemProps() throws IOException{
    String endpoint = "/rest/systemprops";
    return proxy.sendGetRequest(microservice1, endpoint);
  }


  @Path("/systeminfo")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject systemInfo() throws Exception{
    String endpoint = "/rest/systeminfo";
    return proxy.sendGetRequest(microservice1, endpoint);
  }

  @GET
  @Path("/shipList")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject listSpaceships() throws Exception{
    String endpoint = "/rest/shipList";
    return proxy.sendGetRequest(microservice2, endpoint);
  }

  @GET
  @Path("/aliens/list")
  @Produces(MediaType.APPLICATION_JSON)

  public JsonObject listAliens() throws IOException{
    String endpoint = "/rest/aliens/list";
    return proxy.sendGetRequest(microservice2, endpoint);
  }

  // This method may not yet work and requires more testing!
  @POST
  @Path("/aliens/new")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject addAliens() throws IOException{
    String endpoint = "/rest/aliens/new";
    return proxy.sendPostRequest(microservice2, endpoint);
  }

  @GET
  @Path("/health1")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject getMicroserviceOneHealth() throws IOException{
    String endpoint = "/health";
    return proxy.sendGetRequest("http://localhost:9090", endpoint);
  }

  @GET
  @Path("/health2")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject getMicroserviceTwoHealth() throws IOException{
    String endpoint = "/health";
    return proxy.sendGetRequest("http://localhost:9091", endpoint);
  }

  @GET
  @Path("/metrics1")
  @Produces(MediaType.TEXT_PLAIN)
  public String getMicroserviceOneMetrics(@HeaderParam("Authorization") String auth) {
    String endpoint = "/metrics";
    System.out.println("auth: " + auth);
    return proxy.sendStringGetRequest("https://localhost:9490", endpoint, auth);
  }
    
  @GET
  @Path("/metrics2")
  @Produces(MediaType.TEXT_PLAIN)
  public String getMicroserviceTwoMetrics(@HeaderParam("Authorization") String auth) {
    String endpoint = "/metrics";
    System.out.println("auth: " + auth);
    return proxy.sendStringGetRequest("https://localhost:9491", endpoint, auth);
  }
} 
