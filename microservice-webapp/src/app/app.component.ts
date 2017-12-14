// The app component is the root component of the application, 
// it defines the root tag of the app as <app></app> with the selector property.



import { Component } from '@angular/core';
import {HttpClient} from '@angular/common/http'
import {HttpHeaders} from '@angular/common/http'
import { OnInit } from '@angular/core';



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']

})


export class AppComponent implements OnInit {
  title = 'app';
  serverEndpoint = "http://localhost:9080/LibertyGateway-1.0/rest/systemprops";
  serverEndpoint2 = "http://localhost:9080/LibertyGateway-1.0/rest/shipList";
  healthOneEndpoint = "http://localhost:9080/LibertyGateway-1.0/rest/health1";
  healthTwoEndpoint = "http://localhost:9080/LibertyGateway-1.0/rest/health2";
  metricsOneEndpoint = "http://localhost:9080/LibertyGateway-1.0/rest/metrics1";
  metricsTwoEndpoint = "http://localhost:9080/LibertyGateway-1.0/rest/metrics2";

  os: string;
  osVersion: string;
  osLanguage: string;
  user: string;
  userHome: string;
  shipName: string;
  shipType: string;
  outcome1: string;
  outcome2: string;
  metricsOne: string = undefined;
  metricsTwo: string = undefined;


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


  healthStatusOne() {
    if (this.outcome1 === undefined) {
      this.http.get(this.healthOneEndpoint,{responseType: 'json'}).subscribe(data => {
        console.log("data - health 1: ", data);
        this.outcome1 = data["outcome"];
      });
    } else {
      this.outcome1 = undefined;
    }
  }

  healthStatusTwo() {
    this.http.get(this.healthTwoEndpoint, {responseType: 'json'}).subscribe(data => {
      console.log("data - health 2", data);
      this.outcome2 = data["outcome"]
    });
  }

  metricsStatusOne() {
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

  metricsStatusTwo() {
    var token = "Basic " + btoa("confAdmin" + ":" + "microprofile");
    this.http.get(this.metricsTwoEndpoint, 
      { 
        headers: new HttpHeaders().set('Authorization', token), 
        responseType: 'text'
      })
      .subscribe(data => {
        this.metricsTwo = data;
        this.metricsTwo = this.metricsTwo.replace(/(base:)+/g, '\nbase:').replace(/(application:)+/g, '\napplication:');
      });
  }

  closeStatusMetricsOne(){
    this.metricsOne = undefined;
  }

  closeStatusMetricsTwo(){
    this.metricsTwo = undefined;
  }
}


