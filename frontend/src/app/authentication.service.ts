import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private _serverUrl = "http://localhost:8081";  // TODO : Use docker variable

  /**
   * Empty constructor
   * @param _httpClient Client for HTTP requests
   */
  constructor(private _httpClient: HttpClient) { }

  /**
   * Checks credentials with server for login
   * @param username Username
   * @param password Password
   * @returns Response from server
   */
  checkLogin(username: string, password: string): Observable<any> {
    let data = {"username": username, "password": password};

    return this._httpClient.post(this._serverUrl + "/auth/login", data);
  }
}
