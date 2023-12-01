import { inject } from "@angular/core";
import {
  CanActivateFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  Router,
} from "@angular/router";
import { AuthenticationService } from "../login/authentication.service";

export const loginPageGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): boolean | UrlTree => {
  const isConnected = inject(AuthenticationService).isConnected();

  if (isConnected) {
    // utilisateur connecté -> redirection vers la page de chat
    return inject(Router).parseUrl("/chat");
  }

  // navigation dans la page de connexion permise
  return true;
};
