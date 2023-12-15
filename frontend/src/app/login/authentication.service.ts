import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, firstValueFrom } from "rxjs";
import { UserCredentials } from "./model/user-credentials";
import { HttpClient } from "@angular/common/http";
import { environment } from "src/environments/environment";
import { LoginResponse } from "./model/login-response";

@Injectable({
  providedIn: "root",
})
export class AuthenticationService {
  static KEY = "username";

  private username = new BehaviorSubject<string | null>(null);

  constructor(private httpClient: HttpClient) {
    this.username.next(localStorage.getItem(AuthenticationService.KEY));
  }

  async login(userCredentials: UserCredentials) {
    const response = await firstValueFrom(
      this.httpClient.post<LoginResponse>(
        `${environment.backendUrl}/auth/login`,
        userCredentials,
        { withCredentials: true }
      )
    );

    this.setStoredUsername(response.username);
    this.username.next(response.username);
  }

  async logout() {
    try {
      await firstValueFrom(
        this.httpClient.post(`${environment.backendUrl}/auth/logout`, null, {
          withCredentials: true,
        })
      );
    } finally {
      this.setStoredUsername(null);
      this.username.next(null);
    }
  }

  getUsername(): Observable<string | null> {
    return this.username.asObservable();
  }

  isConnected(): boolean {
    return this.getStoredUsername() != null;
  }

  private getStoredUsername(): string | null {
    return localStorage.getItem(AuthenticationService.KEY);
  }

  private setStoredUsername(username: string | null) {
    if (username != null) {
      localStorage.setItem(AuthenticationService.KEY, username);
    } else {
      localStorage.removeItem(AuthenticationService.KEY);
    }
  }
}
