# MicroProfile Tutorial (Still Under Construction)

###About this module

In this module, we will demonstrate how to use the MicroProfile Config feature. This feature enables users to separate configuration from code to enable microservices to be successfully run in different environments which require different configurations. This helps to ensure that their microservices are highly portable. MP Config is a solution to externalise configuration from microservices. This module is a continuation of module 3 but we have provided the finished code to module 3 for you in this module 4 branch.

1.  Firstly, we need to set our static configuration properties in a static configuration properties file (this could be one of three pre-existing config files).
2.  Secondly, we need to create a custom configuration file to enable dynamic configuration properties that are able to be updated without restarting the server.
3.  Finally, we need to test our new dynamic and static configuration properties by changing them. We will be testing the static values by restarting the sever and checking that the config values had been updated and for the dynamic config values that it updates without restarting the server.

If you need more help in any of the concepts and methods used in this module, please see the two interactive guides on OpenLiberty.io.guides: 
1.  Using MicroProfile Config for static configuration injection [LINK]
2.  Advancing the use of MicroProfile Configuration [LINK]

## Before We Start

Make sure you have all the prerequisites installed before continuing with these instructions. Clone down this repository
https://github.com/OpenLiberty/tutorial-microprofile.git
and navigate to the repository you just cloned. Change branch to module 3 *tutorial-microprofile*
*git checkout module4*
 
### Enabling Static Configuration Properties
In this section we will be to configure the HTTP and HTTPS port values that the gateway runs on in a static configuration file. 
1.  Make sure you are in the root directory of the repository you downloaded and navigate into the following directory *microservice-gateway/src/main/java/liberty/config* and open the file called *bootstrap.properties*
2.  Add the following to this file:
*default.http.port=9080*
*default.https.port=9081*
3.  Navigate to the *server.xml* file in *microservice-gateway/src/main/liberty/config*. In this file add the feature ```<feature>mpConfig-1.1</feature>```. 
In the same file, replace the httpEndpoint method with the following code:
```<httpEndpoint host="*"id="defaultHttpEndpoint"httpsPort="${default.https.port}"httpPort="${default.http.port}"/>```
This will enable the server.xml to peer into the bootstrap.properties file to retrieve the HTTP and HTTPS port values.
 
### Creating a Custom Configuration Properties Source

Default config sources (like bootstrap.properties) are static and fixed on application starting, so you cannot modify them while the server is running. However, you can externalize configuration data out of the application package so that the service updates configuration changes dynamically.

1.  Add a CustomConfig.json file outside of your application. In our example the file path for this json is: 
*microservice-gateway/CustomConfigSource.json*

2.  In this json file add the following code: 

```
{    “config_ordinal”:700,
    “userID”:100
}
```

The config_ordinal property sets the ordinal of the json file, meaning that it determines the order in which the files are prioritised (higher ordinals are prioritised over lower ordinals and so can overwrite property values of the same name in these files with lower ordinals).

3.  Next, we need to create our own custom configuration source by implementing the *org.eclipse.microprofile.config.spi.ConfigSource* interface and using the *java.util.ServiceLoader* mechanism. To do this, navigate to *microservice-gateway/src/main/java/application* and create a file called *CustomConfigSource.java* and input:

[CODE]

The ```setProperties()``` private method reads the key value pairs from the *CustomConfigSource.json* JSON file and writes the information into a map.

4.  To register the custom configuration source, add the full class name in the *start/src/main/resources/META-INF/services/org.eclipse*.microprofile.config.spi.ConfigSource file:

```application.CustomConfigSource```
 
## Enabling Dynamic Configuration Properties

1.  In *microservice-gateway/src/main/java/application/rest* create a new file called *Configuration.java*. In this file input the following:
```
package application.rest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.json.JsonObject;
import javax.inject.Inject;
import javax.json.Json;
import javax.enterprise.context.RequestScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.Config;
import javax.inject.Provider;

@Path("/")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class Configuration {
  @Inject
  private Config config;
  @Inject @ConfigProperty(name="userID", defaultValue="1000")
  private Provider<Integer> userID;
  @GET
  @Path("/user")
  public JsonObject getUserID() {
    JsonObject value = Json.createObjectBuilder()
    .add("userID", userID.get())
    .build();
    return value;
  }
}
```

2. Navigate to the *Gateway.java* file in the same folder and underneath 
```@Inject
    private Proxy proxy;
``` 
input the following:

    ```  @Inject
            private Configuration conf;
    ```
 
3. Then in *Gateway.java*, inside the *public class Gateway*, add the get request:

    ```@GET
        @Path("/user")
        @Produces(MediaType.APPLICATION_JSON)
        public JsonObject getUserId() throws Exception{
            System.out.println("User ID: " + conf.getUserID());
            return conf.getUserID();
            }
    ```

4. After this change file location to */microservice-webapp/src/app* and enter into the *app.component.html* file. In this file we need to add an extra row in the *Spaceship Details* table for our new UserID field. To do this find the *Spaceship Details* table and underneath the UserHome row, add the following code:

    ``` <tr>
          <th> User ID </th>
          <td> {{userID}} </td>
        </tr>
    ```

5. To ensure that our angular front-end can access this UserID endpoint we need to add and configure the endpoint in our *app.component.ts* file, which can be found in the same folder as *app.component.html*. In this file we need to add three things:
    1) Add the endpoint where all other endpoints are listed:
        ```userIDEndpoint = "http://localhost:9080/LibertyGateway-1.0/rest/user";```
    2) Add the output type expected from calling this endpoint where the other output types for all other endpoints are also listed:
        ```userID: string;```
    3) Add:
        ```this.http.get(this.userIDEndpoint, {responseType: 'json'}).subscribe(data => {
                this.userID = data["userID"]
            });
        ```
        This should be added inside ngOnInit(){}, underneath:
        ```this.http.get(this.serverEndpoint2, {responseType: 'json'}).subscribe(data => {
                this.shipName = data["shipName"]
                this.shipType = data["shipType"]

            });
         ``` 

Now every time the app is refreshed the UserID value will be updated without having to update the server and restart the application. In order to see this in action, launch your application, then enter into the *Configuration.java* file in *microservice-gateway/src/main/java/application/rest*. In this file update the value assigned to UserID to be any integer. Save this file and go back to the launched application in your browser. Now refresh your application's webpage and you should see that this value has updated itself without having to restart your servers. This is dynamic configuration using MP Config.

