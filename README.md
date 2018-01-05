# Module 2
## Still under construction!!!
## About this module
In this module we will demonstrait how to use the MicroProfile fallback feature. This is usefull when you have a problem with one of your micro-services and they become unreachable or unresponsive. This module is a continuation of module 1 but we have provided the finished code to module 1 for you in the module2 branch.

1. Firstly we will need to create a fallback method that will called when we can no longer talk to our microservice.
2. Then we will need to create a fallback microservice that can be used when the first desired on is no longer reachable.
3. Finally we need to test this is working by shutting down micro-service two and trying to comunicate with our space station from the front-end. If we still recieve data back from the space station then we know our fallback code is working.

![Module2](/images/module2.png)

## Before We Start
Make sure you have all the prerequisites installed before continuing with these instructions.
Clone down this repository 

`https://github.com/OpenLiberty/tutorial-microprofile.git` 

and navigate to the repository you just cloned and change branch to module 1 `tutorial-microprofile`

`git checkout module2`

## Adding The Fallback Code
1. Make sure you are in the root directory of the repository you downloaded and navigate into the following directoy `microservice-gateway/src/main/java/application/rest` and open the file called `Proxy.java`
2. We now need to add the following import to the top of the file:
```
import javax.enterprise.context.ApplicationScoped;
```
3. Next we need to change the application scope !Need reason why!!!! by adding the following annotation above the class
```
@ApplicationScoped
public class Proxy{
```
4. Next we need to add annotations to the methods that make the http connections to our back-end microservices:
```
  @Retry(retryOn=NullPointerException.class, maxRetries=2)
  @Fallback(fallbackMethod= "sendFaultGetRequest")
```
The `@Retry` annotation allows you to set what exception you want your method to fail with and how many times you want to retry before the fallback method is called.

The `@Fallback` annotation allows you to set what method is called when the set Exception is thrown.

5. Now we need to add our fallback methods to the Proxy.java class that are called when we are unable to communicate with our desired micro-service:
```
  //This method is used as a fallback if the above method fails to connect to the desired microservice
  public JsonObject sendFaultGetRequest(String server, String endpoint) {
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    String json = null;

    try {
      System.out.println("Attempting to connect to backup microservice!");
      URL resetEndpoint = new URL(backupMicroservice1 + endpoint);
      connection = (HttpURLConnection) resetEndpoint.openConnection();
      connection.setRequestMethod("GET");

      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder jsonSb = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null) {
        jsonSb.append(line);
      }
      json = jsonSb.toString();
    } 
    catch (Exception e) {
      // Need to return better exception here!!!
      System.out.println("Backup Microservice connection failed! : " + backupMicroservice1);
      e.printStackTrace();
    }

    public JsonObject sendFaultPostRequest(String server, String endpoint) {
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    String json = null;
    try {
      URL resetEndpoint = new URL(backupMicroservice1 + endpoint);
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
      // Need to return better exception here!!!
      System.out.println("Backup Microservice connection failed! : " + backupMicroservice1);
      e.printStackTrace();
    }
      //return json;
      JsonReader jsonReader = Json.createReader(new StringReader(json));
      JsonObject object = jsonReader.readObject();
      jsonReader.close();
      return object;
  }
```

## Creating The Fallback Micro-Service
Some basic files have been provided for you such as a basic pom file for building our application with maven, a License file, some basic Open Liberty configuration in the form of xml and the directory structure required for this microservice. More information about these files can be found in the README in the master branch in this repo.
