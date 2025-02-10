import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from '../authentication.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  signupData: FormGroup;

  /**
   * Constructor of signup component
   * @param _auth Authentication service for credential checks
   * @param routers App routers
   */
  constructor( private _auth: AuthenticationService, private routers: Router) {
    this.signupData = new FormGroup({username: new FormControl(), email: new FormControl(), password: new FormControl()});
  }

  /**
   * Loads signup data on init
   */
  ngOnInit() {
    this.signupData = new FormGroup({username: new FormControl(), email: new FormControl(), password: new FormControl()});
  }

  /**
   * Try signing up with the given credential
   */
  signUp(): void {
    let username = this.signupData.value.username;
    let email = this.signupData.value.email;
    let password = this.signupData.value.password;

    this._auth.checkSignup(username, email, password);
  }
}
