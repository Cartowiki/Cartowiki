import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from '../authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginData: FormGroup;

  /**
   * Constructor of login component
   * @param _auth Authentication service for credential checks
   * @param routers App routers
   */
  constructor( private _auth: AuthenticationService, private routers: Router) {
    this.loginData = new FormGroup({username: new FormControl(), password: new FormControl()});
  }

  /**
   * Loads login data on init
   */
  ngOnInit() {
    this.loginData = new FormGroup({username: new FormControl(), password: new FormControl()});
  }

  /**
   * Try logging in with the given credential
   */
  logIn(): void {
    let username = this.loginData.value.username;
    let password = this.loginData.value.password;

    this._auth.checkLogin(username, password).subscribe({
      next: data => {
          console.log(data.token);
      },
      error: error => {
          console.warn(error.error.message);
      }
    });
  }
}
