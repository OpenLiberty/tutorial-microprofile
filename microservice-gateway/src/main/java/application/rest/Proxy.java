package application.rest;


import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.Json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Proxy{

  //Hardcoded URL needs moving into an enviroment variable
  String backupMicroservice1 = "http://localhost:9092/LibertyMicroServiceTwoFallback-1.0";

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
}