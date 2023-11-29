import { Injectable } from "@angular/core";
import { CanActivate, UrlTree } from "@angular/router";
import { AuthenticationService } from "../login/authentication.service";
import { Router } from "@angular/router";

@Injectable({
  providedIn: "root",
})
export class ChatPageGuard implements CanActivate {
  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) {}

  canActivate(): boolean | UrlTree {
    const isConnected = this.authService.isConnected();

    if (!isConnected) {
      // utilisateur non connecté -> redirection vers la page de connexion
      return this.router.parseUrl("/login");
    }

    // navigation sur la page de chat permise
    return true;
  }
}
