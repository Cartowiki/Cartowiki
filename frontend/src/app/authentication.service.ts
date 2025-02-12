import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TokenService } from './token.service';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private apiUrl = environment.API_URL;

  /**
   * Autowired constructor
   * @param _httpClient Client for HTTP requests
   * @param _tokenService Service for token mangement
   */
  constructor(private _httpClient: HttpClient, private _tokenService: TokenService) { }

  /**
   * Send a POST request to the server
   * @param path Ressource path to reach
   * @param data Data to send
   * @returns Server's response
   */
  private postRequest(path: string, data: any): Observable<any> {
    return this._httpClient.post(this.apiUrl + path, data);
  }

  /**
   * Checks credentials with server for login
   * @param username Username
   * @param password Password
   */
  checkLogin(username: string, password: string) {
    let credentials = {"username": username, "password": password};

    // Remove old token
    this._tokenService.removeToken();

    this.postRequest("/auth/login", credentials).subscribe({
      next: data => {
          if (data.token) {
            this._tokenService.setToken(data.token);
            console.log("Authentification réussie");
          }
          else {
            console.error("Erreur lors de l'authentification");
          }
      },
      error: error => {
        console.error(error.error.message);
      }
    });
  }

  /**
   * Checks credentials with server for signup
   * @param username Username
   * @param email Email address
   * @param password Password
   */
  checkSignup(username: string, email: string, password: string) {
    let informations = {"username": username, "email": email, "password": password};

    this.postRequest("/auth/signup", informations).subscribe({
      next: data => {
        console.log("Création du compte réussie");
      },
      error: error => {
        console.error(error.error.message);
      }
    });
  }
}
