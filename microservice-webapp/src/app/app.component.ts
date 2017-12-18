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
        this.outcome1 = "DOWN";

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

    
    this.metricsOne = `
    # TYPE base:classloader_total_loaded_class_count counter
    # HELP base:classloader_total_loaded_class_count Displays the total number of classes that have been loaded since the Java virtual machine has started execution.
    base:classloader_total_loaded_class_count 9198
    # TYPE base:gc_global_count counter
    # HELP base:gc_global_count Displays the total number of collections that have occurred. This attribute lists -1 if the collection count is undefined for this collector.
    base:gc_global_count 8
    # TYPE base:cpu_system_load_average gauge
    # HELP base:cpu_system_load_average Displays the system load average for the last minute. The system load average is the sum of the number of runnable entities queued to the available processors and the number of runnable entities running on the available processors averaged over a period of time. The way in which the load average is calculated is operating system specific but is typically a damped time-dependent average. If the load average is not available, a negative value is displayed. This attribute is designed to provide a hint about the system load and may be queried frequently. The load average may be unavailable on some platform where it is expensive to implement this method.
    base:cpu_system_load_average -1.0
    # TYPE base:thread_count counter
    # HELP base:thread_count Displays the current number of live threads including both daemon and non-daemon threads.
    base:thread_count 56
    # TYPE base:classloader_current_loaded_class_count counter
    # HELP base:classloader_current_loaded_class_count Displays the number of classes that are currently loaded in the Java virtual machine.
    base:classloader_current_loaded_class_count 9198
    # TYPE base:gc_scavenge_time_seconds gauge
    # HELP base:gc_scavenge_time_seconds Displays the approximate accumulated collection elapsed time in milliseconds. This attribute displays -1 if the collection elapsed time is undefined for this collector. The Java virtual machine implementation may use a high resolution timer to measure the elapsed time. This attribute may display the same value even if the collection count has been incremented if the collection elapsed time is very short.
    base:gc_scavenge_time_seconds 0.23900000000000002
    # TYPE base:jvm_uptime_seconds gauge
    # HELP base:jvm_uptime_seconds Displays the start time of the Java virtual machine in milliseconds. This attribute displays the approximate time when the Java virtual machine started.
    base:jvm_uptime_seconds 2958.926
    # TYPE base:memory_committed_heap_bytes gauge
    # HELP base:memory_committed_heap_bytes Displays the amount of memory in bytes that is committed for the Java virtual machine to use. This amount of memory is guaranteed for the Java virtual machine to use.
    base:memory_committed_heap_bytes 3.6503552E7
    # TYPE base:thread_max_count counter
    # HELP base:thread_max_count Displays the peak live thread count since the Java virtual machine started or peak was reset. This includes daemon and non-daemon threads.
    base:thread_max_count 59
    # TYPE base:cpu_available_processors gauge
    # HELP base:cpu_available_processors Displays the number of processors available to the Java virtual machine. This value may change during a particular invocation of the virtual machine.
    base:cpu_available_processors 8
    # TYPE base:thread_daemon_count counter
    # HELP base:thread_daemon_count Displays the current number of live daemon threads.
    base:thread_daemon_count 54
    # TYPE base:gc_scavenge_count counter
    # HELP base:gc_scavenge_count Displays the total number of collections that have occurred. This attribute lists -1 if the collection count is undefined for this collector.
    base:gc_scavenge_count 145
    # TYPE base:classloader_total_unloaded_class_count counter
    # HELP base:classloader_total_unloaded_class_count Displays the total number of classes unloaded since the Java virtual machine has started execution.
    base:classloader_total_unloaded_class_count 0
    # TYPE base:memory_max_heap_bytes gauge
    # HELP base:memory_max_heap_bytes Displays the maximum amount of heap memory in bytes that can be used for memory management. This attribute displays -1 if the maximum heap memory size is undefined. This amount of memory is not guaranteed to be available for memory management if it is greater than the amount of committed memory. The Java virtual machine may fail to allocate memory even if the amount of used memory does not exceed this maximum size.
    base:memory_max_heap_bytes 5.36870912E8
    # TYPE base:cpu_process_cpu_load_percent gauge
    # HELP base:cpu_process_cpu_load_percent Displays the 'recent cpu usage' for the Java Virtual Machine process.
    base:cpu_process_cpu_load_percent -1.0
    # TYPE base:memory_used_heap_bytes gauge
    # HELP base:memory_used_heap_bytes Displays the amount of used heap memory in bytes.
    base:memory_used_heap_bytes 3.2842064E7
    # TYPE base:gc_global_time_seconds gauge
    # HELP base:gc_global_time_seconds Displays the approximate accumulated collection elapsed time in milliseconds. This attribute displays -1 if the collection elapsed time is undefined for this collector. The Java virtual machine implementation may use a high resolution timer to measure the elapsed time. This attribute may display the same value even if the collection count has been incremented if the collection elapsed time is very short.
    base:gc_global_time_seconds 0.076
    `;
    
    this.metricsOne = this.metricsOne.replace(/^.*#.*$/mg, "").replace(/\s+/g, "").replace(/(base:)+/g, '\nbase:');
    //this.metricsOne = this.metricsOne.replace("base:", "\nbase:");

    console.log(this.metricsOne);

  }
  closeStatus(){
    this.metricsOne = undefined;
  }
}


