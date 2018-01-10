# Module 5

### About this module 

This module will show you how to take advantage of the Health and Metrics features added into MicroProfile 1.2 to see if your microservices are responding and provide metric information regarding these services such as CPU load, JVM usage and many other useful information.
 
 ![Module 5](/images/module5.png)

### Before we start

Make sure you have all the prerequisites installed before continuing with these instructions. Clone down this repository

`https://github.com/OpenLiberty/tutorial-microprofile.git`

and navigate to the repository you just cloned and change branch to module 5 tutorial-microprofile

git checkout `module5`


### Health

A health check is a MicroProfile Health API implementation that is provided by a microservice. We use health checks to assess the health of a service.

Some basic files have been provided for you such as a basic pom file for building our application with maven, a License file, some basic Open Liberty configuration in the form of xml and the directory structure required for this microservice.

### Assessing health of microservice

1. Navigate into the following directory `microservice-gateway/src/main/java/application/rest/Gateway.java`
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

3. Next we need to add the required REST API call.
```
  @GET
  @Path("/health1")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonObject getMicroserviceOneHealth() throws IOException{
    String endpoint = "/health";
    return proxy.sendGetRequest("http://localhost:9090", endpoint);
  }
  ```

4. To display the health from the microservice go to `http://localhost:9090/health`
5. To display the health from the gateway go to `http://localhost:9080/LibertyGateway-1.0/rest/health1`


### Metrics

You can use the MicroProfile metrics API to add metrics to your applications. The MicroProfile metrics API is similar to the Dropwizard metrics API.

When you use the MicroProfile metrics API, you can perform the following functions.
1. Create and register metrics by using Contexts and Dependency Injection (CDI).
2. Add metadata to describe metrics.
3. Access useful JVM/server base metrics.
4. Access the registries that contain all registered metrics and metadata.


### Displaying metrics

1. We navigate into `microservice-one/src/main/java/application/rest` and create a new file `JaxrsManager.java`.
2. Add the required imports
```
package application.rest;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Timer;
import javax.json.JsonObject;
import javax.json.Json;
```

3. Now we need to paste the code below into the file in order to initialize the registry and the metadata that we are using to record the metrics for the microservice in the next steps.
```
public class JaxrsManager {

  private MetricRegistry registry;
  private Timer testDataCalcTimer;
  
  public JaxrsManager() {
    //this.jaxrsManager = jaxrsManager;
    registry = JaxrsApplication.registry;
    setupMetrics();       
  }
    
  private void setupMetrics() {
    // Timer
    Metadata testDataCalcTimerMetadata = new Metadata(
          "testDataCalcTimer",                             // name
          "Test Data Calculation Time",                    // display name
          "Processing time to find the test data",         // description
          MetricType.TIMER,                                   // type
          MetricUnits.NANOSECONDS);                           // units
          testDataCalcTimer = registry.timer(testDataCalcTimerMetadata);
  }
    
  /**
    * getTestData - get the testing data
    *
    * @return JsonObject
    */
  public JsonObject getTestData() {
      
    // Start timing here
    Timer.Context context = testDataCalcTimer.time();
        
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
      
      // Stop timing
      context.close();
      return value;
    }
}
```

4. Next we need to update the `LibertyRestEnpoint.java` which is located in the same folder as the `JaxrsManager.java` to look like the example below adding the @ApplicationScoped annotation which creates the object once for the duration of the application. The @Timed annotation denotes a timer, which tracks duration of the annotated object. We add these annotations so we can register the metrics for the endpoints in the file.
```
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
  @Path("/systemprops")
  @Timed
  public JsonObject getSystemProps() {
      JsonObject value = Json.createObjectBuilder()
      .add("os", System.getProperty("os.name"))
      .add("osVersion", System.getProperty("os.version"))
      .add("osLanguage", System.getProperty("user.language"))
      .add("user", System.getProperty("user.name"))
      .add("userHome", System.getProperty("user.home"))
      .build();
      return value;
  }

  @GET
  @Path("/systeminfo")
  @Timed
  public JsonObject getSystemInfo() {
      JsonObjectBuilder builder = Json.createObjectBuilder();
      System.getProperties()
                .entrySet()
                .stream()
                .forEach(entry -> builder.add((String)entry.getKey(),
                                              (String)entry.getValue()));
      JsonObject jsnobj = builder.build();
      
      //Get specific system props from our JSON Object
      JsonObject value = Json.createObjectBuilder()
      .add("os", jsnobj.getString("os.name"))
      .add("osVersion", jsnobj.getString("os.version"))
      .add("osLanguage", jsnobj.getString("user.language"))
      .add("user", jsnobj.getString("user.name"))
      .add("userHome", jsnobj.getString("user.home"))
      .build();
      return value;
  }
}
```

5. Navigate into the following directory `microservice-one/src/main/java/application/rest/JaxrsApplication.java`.

6. Add the required imports
```
package application.rest;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.microprofile.metrics.MetricRegistry;
```

7. We want to use a single JaxrsApplication class to manage the endpoint in jaxrs.
```
@ApplicationPath("/rest/*")
public class JaxrsApplication extends Application {

  public static MetricRegistry registry; 
  
  private Set<Class<?>> classes = new HashSet<Class<?>>();
  private Set<Object> singletons = new HashSet<Object>();

  public JaxrsApplication() {
      //Uses a single LibertyRestEndpoint class to manage the endpoint in jaxrs

      singletons.add(new LibertyRestEndpoint());
  }
  
  @Inject
  public void setup(MetricRegistry registry) {
      JaxrsApplication.registry = registry;
  }
  
  @Override
  public Set<Class<?>> getClasses() {
      return classes;
  }

  @Override
  public Set<Object> getSingletons() {
      return singletons;
  }    
}
``` 

8. To display the metrics go to `http://localhost:9090/metrics` which will be redirected to `https://localhost:9490/metrics` and will keep the data private. 

9. Next we will be asked to enter our username and password which should match the username and password from the `microservice-one/src/main/liberty/server.xml`.

10. When we go to `http://localhost:9090/metrics` we see JVM information. If we want to display metrics for our application we need to run our application first and then go to `http://localhost:9090/metrics/application`.


### Application example that uses health and metrics endpoint of the microservice

1. Navigate into the following directory `microservice-webapp/src/app/app.component.ts` 

2. First declare the health and metrics endpoints for the microservice like this: 
```
healthOneEndpoint = "http://localhost:9080/LibertyGateway-1.0/rest/health1";
metricsOneEndpoint = "http://localhost:9080/LibertyGateway-1.0/rest/metrics1";
```

3. Next declare a variable where to store the fetched health and metrics data from the microservice in the main scope of the class by adding the following: 
```
healthOne: string;
metricsOne: string = undefined;
```

4. The next step is to declare a function which fetches the health and metrics data from microservice one and store the outcome in the above variables. For the metrics we need to provide username and password which should match the username and password from the `microservice-one/src/main/liberty/server.xml`.
```
healthStatusOne() {
  if (this.healthOne === undefined) {
    this.http.get(this.healthOneEndpoint,{responseType: 'json'}).subscribe(data => {
      this.healthOne = data["outcome"];
    });
  } else {
    this.healthOne = undefined;
  }
}

metricsStatusOne() {
  //"confAdmin" is the username and "microprofile" is the password
  var token = "Basic " + btoa("confAdmin" + ":" + "microprofile");
  this.http.get(this.metricsOneEndpoint, 
    { 
      headers: new HttpHeaders().set('Authorization', token), 
      responseType: 'text'
    })
    .subscribe(data => {
      this.metricsOne = data;
      this.metricsOne = this.metricsOne.replace(/(base:)+/g, '\nbase:').replace(/(application:)+/g, '\napplication:');
    });
  }
```

5. Navigate into the following directory `microservice-webapp/src/app/app.component.html` 

In the app.component.html you can write you own user interface, here is an example: 

After the spaceship details table add the following inside the section
```
  <button class="health-metrics" type="button" (click)="healthStatusOne()">Health Status</button>
  <h2 *ngIf="healthOne !== undefined" class="health-{{healthOne.toLowerCase()}}">{{healthOne}}</h2> 
  <button class="health-metrics" type="button" (click)="metricsStatusOne()">Metrics Status</button>
```
Add another section to display the metrics
```
 <section *ngIf="metricsOne !== undefined"  class= "microservice serviceB">
      <div class="metrics">
        <h2> Microservice One Metrics </h2>
        <pre> {{metricsOne}} </pre>
      </div>
 </section>
 ```

6. Navigate into the following directory `microservice-webapp/src/app/app.component.css` to add the styles below:
```
.health-metrics{
  margin-top: 50px;
  margin-left: 250px; 
}

.health-up {
  color: green;
}
```