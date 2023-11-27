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
    localStorage.setItem(AuthenticationService.KEY, response.username);
    this.username.next(response.username);
  }

  async logout() {
    await firstValueFrom(
      this.httpClient.post(`${environment.backendUrl}/auth/logout`, null, {
        withCredentials: true,
      })
    );
    localStorage.removeItem(AuthenticationService.KEY);
    this.username.next(null);
  }

  getUsername(): Observable<string | null> {
    return this.username.asObservable();
  }
}
