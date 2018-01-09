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


