package application.rest;


import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;

import java.net.URL;
import org.eclipse.microprofile.faulttolerance.*;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Proxy{

  //Hardcoded URL needs moving into an enviroment variable
  String backupMicroservice1 = "http://localhost:9092/LibertyMicroServiceTwoFallback-1.0";

  //Method that takes a GET request from the front end and sends that to the desired back-end microservice
  @Retry(retryOn=NullPointerException.class, maxRetries=2)
  @Fallback(fallbackMethod= "sendFaultGetRequest")
  public JsonObject sendGetRequest(String server, String endpoint) throws IOException {
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
      System.out.println("json: " + json);
      System.out.println("message: " + e.getMessage());
      System.out.println("You have not been able to connect to your desired microservice: " + server);
      e.printStackTrace();
      throw e; 
    }
    //return json;
    JsonReader jsonReader = Json.createReader(new StringReader(json));
    JsonObject object = jsonReader.readObject();
    jsonReader.close();
    return object;
  }

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
      System.out.println("resetEndpoint" + resetEndpoint);

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
      System.out.println("Currently using backup microservice!");
      JsonReader jsonReader = Json.createReader(new StringReader(json));
      JsonObject object = jsonReader.readObject();
      jsonReader.close();
      return object;
  }

  //This method is used to send POST requests to the desired back-end microservice
  @Retry(retryOn=NullPointerException.class, maxRetries=2)
  @Fallback(fallbackMethod= "sendFaultPostRequest")
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

  public String sendStringGetRequest(String server, String endpoint, String auth) throws NullPointerException {
    HttpsURLConnection connection = null;
    BufferedReader reader = null;
    String json = null;

    try {
      URL resetEndpoint = new URL(server + endpoint);
      connection = (HttpsURLConnection) resetEndpoint.openConnection();
      connection.setRequestProperty("Authorization", auth);
      // Set request method to GET as required from the API
      connection.setRequestMethod("GET");
      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      System.out.println("TESTING!!!");
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
        //JsonReader jsonReader = Json.createReader(new StringReader(json));
        //JsonObject object = jsonReader.readObject();
        //jsonReader.close();
        return json;
  }
}