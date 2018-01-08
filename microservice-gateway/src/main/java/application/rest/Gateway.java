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
import javax.inject.Inject;
import javax.json.JsonObject;
import java.io.IOException;
import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.Config;
import javax.inject.Provider;

@RequestScoped
@Path("/")
public class Gateway {
  @Inject
  private Config config;
  @Inject @ConfigProperty(name="port", defaultValue="9080")
  private Provider<Integer> port;
  public Integer getPort() {
    return port.get();
  }
  //Server locations! This will be moved to enviroment variables at a later date!
  String microservice1 = "http://localhost:9090/LibertyMicroServiceOne-1.0";
  String microservice2 = "http://localhost:9091/LibertyMicroServiceTwo-1.0";

  @Inject
  private Proxy proxy;
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
    System.out.println("GRACE PORT: " + getPort());
    return proxy.sendGetRequest(microservice1, endpoint);
  }

  @GET
  @Path("/shipList")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject listSpaceships() throws IOException{
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

  @POST
  @Path("/auth")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject checkAuth() throws NullPointerException{
    String endpoint = "/rest/auth";
    return proxy.sendPostRequest("/rest/auth", endpoint);
  }
}
