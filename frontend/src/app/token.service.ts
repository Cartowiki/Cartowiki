import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  // Key to value in sessionStorage
  private TOKEN_KEY = "accessToken";

  /**
   * Empty constructor
   */
  constructor() {}

  /**
   * Set a new token in sessionStorage
   * @param token New token
   */
  public setToken(token: string): void {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  }

  /**
   * Get the token from sessionStorage
   * @returns Current token
   */
  public getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Remove the token from sessionStorage
   */
  public removeToken(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
  }
}
