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

  login(userCredentials: UserCredentials): void {
    try {
      this.httpClient
        .post(`${environment.backendUrl}/auth/login`, userCredentials, {
          withCredentials: true,
        })
        .subscribe((res) => {
          console.log(res);
          localStorage.setItem(
            AuthenticationService.KEY,
            userCredentials.username
          );
        });
      this.username.next(userCredentials.username);
    } catch (err) {
      throw new Error("Erreur de connexion: " + err);
    }
  }

  logout(): void {
    this.httpClient
      .post(`${environment.backendUrl}/auth/logout`, {
        withCredentials: true,
      })
      .subscribe((res) => {
        localStorage.removeItem(AuthenticationService.KEY);
        this.username.next(null);
      });
  }

  getUsername(): Observable<string | null> {
    return this.username.asObservable();
  }
}
