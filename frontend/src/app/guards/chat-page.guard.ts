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
  const isConnected = inject(AuthenticationService).isConnected();

  if (!isConnected) {
    // utilisateur non connecté -> redirection vers la page de connexion
    return inject(Router).parseUrl("/login");
  }

  // navigation sur la page de chat permise
  return true;
};
