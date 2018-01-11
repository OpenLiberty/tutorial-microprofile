# Module 1
## Still under construction!!!
## About this module
1. Firstly we will create the gateway service that will enable our front end microservice to connect to our two back end micro-services.
2. Then we will create our first back end micro-service (Microservice A) that will provide some basic JSON data along with some system properties about the space ship it is associated with.
3. Then we will create our second back end micro-service that will provide more JSON information about the space station.
4. Lastly we will create a front end that will act as your spaceships on board computer. 

![Module 1](/images/module1.png)

## Before we start
Make sure you have all the prerequisites installed before continuing with these instructions.
Clone down this repository 

`https://github.com/OpenLiberty/tutorial-microprofile.git` 

and navigate to the repository you just cloned and change branch to module 1 `tutorial-microprofile`

`git checkout module1`
 
## Creating the Gateway
Some basic files have been provided for you such as a basic pom file for building our application with maven, a License file, some basic Open Liberty configuration in the form of xml and the directory structure required for this microservice.

1. Navigate into the following directory
`microservice-gateway/src/main/java/application/rest` (to do this you will need to create new java, application and rest folders) and create a new file inside the rest folder called `Gateway.java`
2. We will use this file for listing all the separate REST API calls we require from our back-end microservices. Firstly add all the required imports needed for this class specified below:

```
package application.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.json.JsonObject;

import javax.inject.Inject;
```
3. Now we need to add the required REST API calls to the class. We are also using CDI to inject the class Proxy which is responsible for connecting to the back-end:
```
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
}
```

4. Now that we have our gateway class we need to create a second class called `Proxy.java` that as mentioned above will act as our connection to the back end.
5. Firstly we need to add the required imports for this class:
```
package application.rest;


import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.Json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.eclipse.microprofile.faulttolerance.*;

import javax.enterprise.context.ApplicationScoped;
```
6. We then need to add the main method to send data to and from the back-end from our web application
```
//Method that takes a GET request from the front end and sends that to the desired back-end microservice
  public JsonObject sendGetRequest(String server, String endpoint) throws NullPointerException {
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    String json = null;

    try {
      URL resetEndpoint = new URL(server + endpoint);
      connection = (HttpURLConnection) resetEndpoint.openConnection();
      // Set request method to GET as required from the API
      connection.setRequestMethod("GET");
      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder jsonSb = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null) {
        jsonSb.append(line);
      }
      json = jsonSb.toString();
    } catch (Exception e) {
      System.out.println("You have not been able to connect to your desired microservice: " + server);
    }
        //return json;
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
        return object;
  }
  ```
  The above code will open a HTTP connection to the back end microservice that you provide with two strings, server is the location of the endpoint so in our case that would be http://localhost:9090 and the endpoint is the specific REST call you require such as /rest/test/

7. Now we need to add the code to send POST request to our back-end:
```
    //This method is used to send POST requests to the desired back-end microservice
  public JsonObject sendPostRequest(String server, String endpoint) {
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    String json = null;

    try {
      URL resetEndpoint = new URL(server + endpoint);
      connection = (HttpURLConnection) resetEndpoint.openConnection();
      // Set request method to GET as required from the API
      connection.setRequestMethod("POST");

      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder jsonSb = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null) {
        jsonSb.append(line);
      }
      json = jsonSb.toString();
    } 
    catch (Exception e) {
      System.out.println("You have not been able to connect to your desired microservice: " + server );
    }
        //return json;
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
        return object;
  }
  ```
8. We now need to add a CORS Filter. CORS stands for Cross-Origin Resource Sharing which is a mechanism that uses additional HTTP headers to let a user gain permission to access selected resources from a server on a different origin (e.g. a different domain) than the site originally uses. The same-origin policy, although very effective in preventing resources from different origins (domains), also prevents legitimate interactions between a server and clients of a known, trusted origin. CORS acts as a technique to relax this same-origin policy and allow Javascript on a web page to consume a REST API served from a different origin (domain). In this example, the webapp is being hosted on a Node server and the back-end is being hosted on an OpenLiberty server. The CORS Filter set up in this module will enable incoming connections from our front-end to our back-end. To do this first create a file named: `CorsFilter.java`
9. Now open that file and paste in the following code:
```
package application.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
    }
}
```
10. The final thing we require to get our gateway working is the addition of the following class `JaxrsApplication.java`. Create the file then add the following code:

```
package application.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class JaxrsApplication extends Application {

}
```
Now you have built your basic gateway that will route traffic from the front-end to your desired back end micro-services.

## Creating Microservice-One

Like with the gateway we have created some basic files required for building the micro-service such as a basic pom file for building our application with maven, a License file, some basic Open Liberty configuration in the form of xml and the directory structure required for this microservice. 

1. Firstly we need to navigate up a directory then into the `microservice-one/src/main/java/application/rest` directory.

2. Now in the same directory we need to create another file `CorsFilter.java` to allow access from our front-end java script web application to access this microservice. Then add the following code to that file:
```
package application.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
    }
}
```
3. Next we need create the `JaxrsApplication.java` file like we did in the Gateway and add the following code to access our endpoint:
```
package application.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class JaxrsApplication extends Application {

}
```
4. Now create a new file called: `LibertyRestEndpoint.java`
5. Open the file and add the required imports:
```
package application.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.json.JsonObject;
import javax.json.Json;
```
6. Now we need to name the class and add some endpoints that return the Object type of JSON by pasting the code bellow into the file:
```
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
```

## Creating Microservice-Two
1. The instructions for creating microservice-two are the same as microservice-one navigate up one directory then into the `microservice-two/src/main/java/application/rest` directory and repeat steps 2-6 from the Creating Microservice-One.
2. Now create a file called LiberyRestEndpoint.java and add the following code to create the REST API calls for your second microservice......NEW CODE TO BE ADDED HERE!!!

## Creating the Web Application
Now that we've created a gateway and two microservices (1 and 2), a webapp is needed to visualise the information in these microservices. To make this easier, a webapp has already been fully developed for this example. 
The webapp used in this example has been created using Angular CLI and is running on a Node server. 

In order to use the webapp provided, it is essential that the pre-requisites previously stated are installed first. 

1. Navigate to *microservice-webapp* in the terminal. This folder contains the pre-developed webapp.

2. Once in this folder enter the following command to serve up/launch the webapp and be able to access it:

    `ng serve`

To check the app has been served up, open any browser and hit the URL http://localhost:4200 and you should see the webapp appear.

 Now that the app has been served up, when you edit anything within the Angular app file structure the webapp will be automatically update itself after saving. It is therefore not necessary to restart your app every time.

If you would like more information on how to create a webapp from scratch using an OpenLiberty server or a Node server and how to connect that to an OpenLiberty back-end please see the following interactive guides on http://openliberty.io.guides :

1. https://openliberty.io/guides/rest-client-angularjs.html
