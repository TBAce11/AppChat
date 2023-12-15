import { inject } from "@angular/core";
import {
  CanActivateFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  Router,
} from "@angular/router";
import { AuthenticationService } from "../login/authentication.service";

export const chatPageGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): boolean | UrlTree => {
  return inject(AuthenticationService).isConnected()
    ? // navigation sur la page de chat permise
      true
    : // utilisateur non connecté -> redirection vers la page de connexion
      inject(Router).parseUrl("/login");
};
