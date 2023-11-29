import { Injectable } from "@angular/core";
import { CanActivate, UrlTree } from "@angular/router";
import { AuthenticationService } from "../login/authentication.service";
import { Router } from "@angular/router";

@Injectable({
  providedIn: "root",
})
export class LoginPageGuard implements CanActivate {
  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) {}

  canActivate(): boolean | UrlTree {
    const isConnected = this.authService.isConnected();

    if (isConnected) {
      // utilisateur connecté -> redirection vers la page de chat
      return this.router.parseUrl("/chat");
    }

    // navigation dans la page de connexion permise
    return true;
  }
}
