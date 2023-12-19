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
  return inject(AuthenticationService).isConnected()
    ? // utilisateur connecté -> redirection vers la page de chat
      inject(Router).parseUrl("/chat")
    : // navigation dans la page de connexion permise
      true;
};
