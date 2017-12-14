
// The alert component passes alert messages to the template whenever a message is received from the alert service. 
// It does this by subscribing to the alert service's getMessage() method which returns an Observable.

import { Component, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { AlertService } from '../_services/index';

@Component({
    moduleId: module.id,
    selector: 'alert',
    templateUrl: 'alert.component.html'
})

export class AlertComponent implements OnDestroy {
    private subscription: Subscription;
    message: any;

    constructor(private alertService: AlertService) { 
        // subscribe to alert messages
        this.subscription = alertService.getMessage().subscribe(message => { this.message = message; });
    }

    ngOnDestroy(): void {
        // unsubscribe on destroy to prevent memory leaks
        this.subscription.unsubscribe();
    }
}