# Module 4 - MP Config (Still Under Construction)

### About this module

This module is a continuation of module 3 however, the finished module 3 code has been provided in this module 4 branch.

This module will demonstrate MicroProfile Config. MP Config is a solution to externalise configuration from microservices, enabling users to separate configuration from code to enable microservices to be successfully run in different environments requiring different configurations. This helps to ensure the high portability of microservices.

1. Firstly, the static configuration properties in a static configuration properties file need to be set (this could be one of three pre-existing config files).
2. Following this, a custom configuration file needs to be created to enable dynamic configuration properties (properties that can be updated without having to restart the server).
3. Finally, the new dynamic and static configuration properties need to be tested.


If more help is needed in regards to any of the concepts and methods used in this module, please see the two interactive guides on http://OpenLiberty.io.guides : 
1. Using MicroProfile Config for static configuration injection [LINK]
2. Advancing the use of MicroProfile Configuration [LINK]

### Before We Start

Please ensure all of the prerequisites have been installed before continuing with these instructions. Clone down this repository
https://github.com/OpenLiberty/tutorial-microprofile.git
and navigate to this repository.
Change branch to module 4 of tutorial-microprofile:

*git checkout module4*



## Enabling Static Configuration Properties
This section will demonstrate how to set the HTTP and HTTPS port values, that the gateway server runs on, as examples of static configuration properties in a static configuration file. 
1. Once in the root directory, navigate into the following directory *microservice-gateway/src/main/java/liberty/config* and open the file called *bootstrap.properties*
This bootstrap.properties file is one of the three preconfigured file spaces in OpenLiberty that can be used for static configuration properties. The other two locations that these static configuration properties can be set are: /META-INF/microprofile-config.properties and the server.env files. Bootstrap.properties file has been used in this example because it allows us to set custom properties that can then 
2. Add the following to this file:
*default.http.port=9080*
*default.https.port=9081*
3. Next, navigate to the *server.xml* file in *microservice-gateway/src/main/liberty/config*. In this file add the feature:
```
<feature>mpConfig-1.1</feature>
```


In the same file, replace the httpEndpoint method with the following code:
```<httpEndpoint host="*"id="defaultHttpEndpoint"httpsPort="${default.https.port}"httpPort="${default.http.port}"/>```
This will enable the server.xml to peer into the bootstrap.properties file to retrieve the HTTP and HTTPS port values.

## Creating a Custom Configuration Properties Source

Default config sources (like bootstrap.properties) are static and fixed upon an application starting up, so cannot be modified while the server is running. However, you can externalize configuration data outside the application package so that the configuration changes are updated dynamically. These properties are aptly named "dynamic configuration properties". This section will demonstrate how to set a dynamic configuration property:

1. The first step in this process is to add a CustomConfig.json file outside of your application. In this tutorial, as an example of where to place this json file, the file path used is: 
*microservice-gateway/CustomConfigSource.json*
Feel free to use the same or a similar file path for this file. However, the exact file path is not important as long as the json file lies outside of the application.

2. In the CustomConfigSource.json file add the following code: 
```
{ “config_ordinal”:700,
  “userID”:100
}
```
The config_ordinal property sets the ordinal of the json file, meaning that it determines the order in which the files are prioritised (higher ordinals are prioritised over lower ordinals and so can overwrite property values of the same name in files with lower ordinals). Having an ordinal of 700 in this file prioritises this file over all other property files.

3. Next, we need to create our own custom configuration source by implementing the *org.eclipse.microprofile.config.spi.ConfigSource* interface and using the *java.util.ServiceLoader* mechanism. To do this, navigate to *microservice-gateway/src/main/java/application* and create a file called *CustomConfigSource.java* and input:

```
package application;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.json.Json;
import java.math.BigDecimal;
import java.util.*;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.eclipse.microprofile.config.spi.ConfigSource;

public class CustomConfigSource implements ConfigSource {
    
  String fileLocation = System.getProperty("user.dir").split("target")[0] + "CustomConfigSource.json";
  private Map<String, String> map = setProperties();

  @Override
  public int getOrdinal() {
      return Integer.parseInt(this.map.get("config_ordinal"));
  }
  @Override
  public Set<String> getPropertyNames() {
    return this.map.keySet();
  }
  @Override
  public Map<String, String> getProperties() {
    return this.map;
  }
  @Override
  public String getValue(String key) {
    return this.map.get(key);
  }
  @Override
  public String getName() {
    return "Custom Config Source: file:" + this.fileLocation;
  }
    
  private Map<String, String> setProperties() {
    Map<String, String> m = new HashMap<String, String>();
    System.out.println(fileLocation);
    String jsonData = this.readFile(this.fileLocation);
    JsonParser parser = Json.createParser(new StringReader(jsonData));
    String key = null;
    while (parser.hasNext()) {
      final Event event = parser.next();
      switch (event) {
      case KEY_NAME:
        key = parser.getString();
        break;
      case VALUE_STRING:
        String string = parser.getString();
        m.put(key, string);
        break;
      case VALUE_NUMBER:
        BigDecimal number = parser.getBigDecimal();
        m.put(key, number.toString());
        break;
      case VALUE_TRUE:
        m.put(key, "true");
        break;
      case VALUE_FALSE:
        m.put(key, "false");
        break;
      default:
        break;
      }
    }
    parser.close();
    return m;
  }
    
  public String readFile(String fileName) {
    String result = "";
    try {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        line = br.readLine();
      }
      result = sb.toString();
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}
```

The ```setProperties()``` private map method reads the key value pairs from the *CustomConfigSource.json* JSON file and writes the information into a map.

4.  To register the custom configuration source in the application, add the following full class name in the *start/src/main/resources/META-INF/services/org.eclipse.microprofile.config.spi.ConfigSource* file:

```
application.CustomConfigSource
```

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

2. Next,navigate to the *Gateway.java* file in the same folder and underneath 
```
@Inject
  private Proxy proxy;
``` 
input the following:

```  
@Inject
  private Configuration conf;
```

3. Then in *Gateway.java*, inside the *public class Gateway*, add the get request:

```
@GET
  @Path("/user")
  @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getUserId() throws Exception{
      System.out.println("User ID: " + conf.getUserID());
        return conf.getUserID();
    }
```

4. After this change file location to */microservice-webapp/src/app* and enter into the *app.component.html* file. In this file an extra row in the *Spaceship Details* table needs to be added for the new UserID field from our dynamic custom config properties file. To do this, find the *Spaceship Details* table and underneath the UserHome row, add the following code:

```
<tr>
  <th> User ID </th>
    <td> {{userID}} </td>
</tr>
```

5. To ensure that the angular front-end can access this UserID endpoint the endpoint in our *app.component.ts* file needs to be added and configured, which can be found in the same folder as *app.component.html*. In this file, three things need to be added:
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

Now every time the app is refreshed the UserID value will be updated without having to update the server and restart the application. In order to see this in action, launch your application, then enter into the *Configuration.java* file in *microservice-gateway/src/main/java/application/rest*. In this file update the value assigned to UserID to be any integer. Save this file and go back to the launched application in the browser. Now refresh your application's webpage and you should see that this value has updated itself without having to restart any of the servers. This is an example of dynamic configuration using MP Config.


