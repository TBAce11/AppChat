import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { UserCredentials } from "./model/user-credentials";
import { HttpClient } from "@angular/common/http";
import { environment } from "src/environments/environment"; // à re-vérifier

@Injectable({
  providedIn: "root",
})
export class AuthenticationService {
  static KEY = "username";

  private username = new BehaviorSubject<string | null>(null);

  constructor(private httpClient: HttpClient) {
    this.username.next(localStorage.getItem(AuthenticationService.KEY));
  }

  async login(userCredentials: UserCredentials): Promise<void> {
    try {
      await this.httpClient.post(
        `${environment.backendUrl}/auth/login`,
        userCredentials,
        {
          withCredentials: true,
        }
      );
      localStorage.setItem(AuthenticationService.KEY, userCredentials.username);
      this.username.next(userCredentials.username);
    } catch (err) {
      throw new Error("Erreur de connexion: " + err);
    }
  }

  async logout(): Promise<void> {
    try {
      await this.httpClient.post(`${environment.backendUrl}/auth/logout`, {
        withCredentials: true,
      });
      localStorage.removeItem(AuthenticationService.KEY);
      this.username.next(null);
    } catch (err) {
      throw new Error("Erreur de connexion: " + err);
    }
  }

  getUsername(): Observable<string | null> {
    return this.username.asObservable();
  }
}
