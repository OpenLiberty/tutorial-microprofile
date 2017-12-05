import { Injectable } from '@angular/core';
import { OnInit } from '@angular/core';
import {HttpClient} from '@angular/common/http'
import {HttpHeaders} from '@angular/common/http'

@Injectable()
    export class DataService implements OnInit {
        title = 'service';
        serverEndpoint = 'http://localhost:9090/LibertyMicroProfile/rest/test'
        results: string[];
      
      
        constructor(private http: HttpClient) {}
        ngOnInit() {
          const headers = new HttpHeaders();  
         // headers.append('Access-Control-Allow-Origin', '*');
          headers.append('responseType','text/plain')
        
          
          this.http.get(this.serverEndpoint, {headers:headers}).subscribe(data => {
            // Read the result field from the JSON response.
            console.log("i got here ")
            this.results = data['results'];
           // console.log("Here is the result " + this.results.)
          });
        }
    }

