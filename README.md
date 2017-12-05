# Module 1
## Still under construction!!!
## About this module
1. Firstly we will create the gateway service that will enable our front end microservice to connect to our two back end micro-services.
2. Then we will create our first back end micro-service (Microservice A) that will provide some basic JSON data along with some system properties about the space ship it is associated with.
3. Then we will create our second back end micro-service that will provide more JSON information about the space station.
4. Lastly we will create a front end that will act as your spaceships on board computer. 

## Before we start
Make sure you have all the prerequisites installed before continuing with these instructions.
Clone down this repository 

`https://github.com/OpenLiberty/tutorial-microprofile.git` 

and navigate to the repository you just cloned and change branch to module 1 `tutorial-microprofile`

`git checkout module1`
 
## Creating the Gateway
Some basic files have been provided for you such as a basic pom file for building our application with maven, a License file, some basic Open Liberty configuration in the form of xml and the directory structure required for this microservice.

1. Navigate into the following directory
`microservice-gateway/src/main/java/application/rest` and create a new file called `Gateway.java`
2. We will use this file for listing all the seperate REST API calls we require from our back-end microservices. Firstly add all the required imports needed for this class specified below:

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
6. We then need to add the main method to send datat to and from the back-end from our web application
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
8. We now need to add a Cors Filter that will enable incoming connections from our java script front-end to our back-end. To do this first create a file named: `CorsFilter.java`
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
3. Next we need create the JaxrsApplication.java file like we did in the Gateway and add the following code to access our endpoint:
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
1. The instructions for creating microservice-two are the same as microservice-one navigate up one directory then into the `microservice-one/src/main/java/application/rest` directory and repeat steps 2-6 from the Creating Microservice-One.
2. Now create a file called LiberyRestEndpoint.java and add the following code to create the REST API calls for your second microservice......NEW CODE TO BE ADDED HERE!!!

## Creating the Web Application
Now that we've created a gateway and microservices A and B, it's time to build a webapp to be able access and easily display the information within these microservices. 

1. Firstly we need to create a basic AngularJS webapp
2. Then we need to use this basic webapp skeleton and customise it to show the information we wish to access from the microservices
3. Finally we need to call API requests, through our gateway service, to the two microservices to be able to access and then display this information on the fron-end to the client.

In order to build our basic AngularJS webapp we'll be using the pre-requisites we've specified previously. Using node and npm we will install the @angular/cli tool which builds all of the files and installs all of the dependencies we will need for this webapp.

In our terminal, enter into the folder in which you want the webapp. Then enter the command:

  `npm install -g @angular/cli`

This may take a little while to run.

After this, run the command:

`ng new my-microprofile-app`

This will create the new AngularJS app file structure and place all of the downloaded files and dependencies within a folder calle "my-microprofile-app".

Then enter into this folder:

`cd my-microprofile-app`

Then enter:

`ng serve`

This will "serve" up, or run, your new angular app.

After serving up your app hit the endpoint 
`http:localhost:4200` which will show your current webapp. Now that you have served the app up, when you edit anything within the Angular app file structure the webapp will be automatically update itself after saving. It is therefore not necessary to restart your app every time.


Now we have a basic AngularJS webapp working we can start to edit the HTML, CSS and component files to customise this webapp to our own needs. 
The first step is to edit our component.html file. 

In the "app.component.html" (./src/app/app.component.html) file delete everything that is currently in there and then copy and paste in the following code:

```
<html>
    <div style="text-align:center">
        <h1>
            Liberty MicroProfile Dashboard
        </h1>
    </div>
    <section class= "microservice serviceA">
      <h2> Your Spaceship Details: </h2>
      <table>
        <tr>
          <th> Operating System </th>
          <td> {{os}} </td>
        </tr>
        <tr>
          <th> OS version </th>
          <td> {{osVersion}} </td>
        </tr>
        <tr>
          <th> OS Language </th>
          <td> {{osLanguage}} </td>
        </tr>
        <tr>
          <th> UserName </th>
          <td> {{user}} </td>
        </tr>
        <tr>
          <th> User Home </th>
          <td> {{userHome}} </td>
        </tr>
        <br>
        <br>
        <img src="assets/logo.png" width="180" align="middle">
      </table>
    </section>
    <section class= "microservice serviceB">
      <h2> The Neighbouring Spaceship's Details: </h2>
      <table>
        <tr>
          <th> Ship Name </th>
          <td> {{shipName}} </td>
        </tr>
        <tr>
          <th> Ship Type </th>
          <td> {{shipType}} </td>
        </tr>
    </table>
  </section>
</html>
```

This will add a title and two tables to our webapp. In the first table we will be calling information from MicroserviceA and in the second table we will be calling information from MicroserviceB.

You can check the progress of the webapp content or style changes by returning to the localhost endpoint and refreshing the page.

The double curly braces used in this HTML is a specific function in Angular that uses TypeScript. These double braces allow us to pull forward properties/data that we will be requesting from the microservices using our app.component.ts and app.module.ts files and inserts these properties into the browser. Angular updates the display when these properties change.

Next we need to edit the css to style our webapp in the same style and theme as the OpenLiberty website. 
To do this open the "app.component.css" file (./src/app/app.component.css) and copy and paste the following code in:
```
.microservice{
  border-color: white;
  height: 400px;
   width: 700px;
   position: fixed;
   outline: white groove;
  }
.serviceA{
  top: 15%;
  left:5%;
  }
.serviceB{
  top: 15%;
  left: 52%;
}

th{
  float:left;
  color: white;
}
td{
  color: white;
}
 
h1 {
  color: #FFFFFF;
  font-family: Arial, Helvetica, sans-serif;
  font-size: 250%;
}

h2 {
  color: #FFFFFF;
  font-family: Arial, Helvetica, sans-serif;
  font-size: 150%;
  text-align: center;
  }
  
.testingButton{
  margin-top: 475px;
}
```

Then in the "styles.css" file (./src/styles.css) we will copy and paste the following code in to style to main html background (i.e. any global style variables):

```
html{
    background-color:  #010b19;
    font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;
  }
  
html {
    height: 100%;
    -webkit-background-size: cover;
    -moz-background-size: cover;
    -o-background-size: cover;
    background-size: cover;
}
```

Now that we have a more visually appealing webapp we can move onto linking it up to our microservices to start populating our tables with data.

To do this we need to enter this code into the "app.component.ts" file (./src/app/app.component.ts):

```
import {Component, OnInit } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http'



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']

})


export class AppComponent implements OnInit {
  title = 'app';
  serverEndpoint = "http://localhost:9080/LibertyGateway-1.0/rest/systemprops";
  serverEndpoint2 = "http://localhost:9080/LibertyGateway-1.0/rest/shipList";
  os: string;
  osVersion: string;
  osLanguage: string;
  user: string;
  userHome: string;
  shipName: string;
  shipType: string;



  constructor(private http: HttpClient) {}
  ngOnInit() {    
    this.http.get(this.serverEndpoint, {responseType: 'json'}).subscribe(data => {
      this.os = data["os"]
      this.osVersion = data["osVersion"]
      this.osLanguage = data["osLanguage"]
      this.user = data["user"]
      this.userHome = data["userHome"]

    });
    this.http.get(this.serverEndpoint2, {responseType: 'json'}).subscribe(data => {
      this.shipName = data["shipName"]
      this.shipType = data["shipType"]

    });
  }
}
```
As you can see from the serverEndpoints we have inputted into the file above, we are actually hitting the gateway and not the microservice directly. Using the code we inputted into the Gateway previously we redirect this HTTP GET request to the correct microservice. This way when a microservice goes down we are able to utilise the gateway service to redirect the front-end's request to a fallback mechanism - this is all in Module 2.

The type of data we are trying to access from each microservices must be specified in this file.
Then using HTTP Get requests we can request this information and store it in order to be able to display it in the front-end tables.

We are using ngOnInit for all the initialization/declaration. The constructor is only used to initialize the class members.

The next step is to import the necessary modules and declarations into the "app.module.ts" file (./src/app/app.module.ts):

```
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    HttpModule,
  ],
  providers: [
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
```
To see how all of these changes have affected your webapp go to `http:localhost:4200`.

Now that we have edited the AngularJS files, with each of the microservice servers up and running you should be able to see the information we assigned to each microservice in the two tables on our webapp.


   