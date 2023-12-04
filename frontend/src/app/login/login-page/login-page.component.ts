import { Component, OnInit, ViewChild } from "@angular/core";
import { UserCredentials } from "../model/user-credentials";
import { AuthenticationService } from "../authentication.service";
import { LoginFormComponent } from "../login-form/login-form.component";
import { Router } from "@angular/router";
import { HttpErrorResponse } from "@angular/common/http";
@Component({
  selector: "app-login-page",
  templateUrl: "./login-page.component.html",
  styleUrls: ["./login-page.component.css"],
})
export class LoginPageComponent implements OnInit {
  @ViewChild(LoginFormComponent, { static: false }) loginFormComponent!: LoginFormComponent;

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  async onLogin(userCredentials: UserCredentials) {
    try {
      await this.authenticationService.login(userCredentials);
      this.router.navigate(["/chat"]);
    } catch (error) {
      this.loginFormComponent.handleLoginError(error);
      if (error instanceof HttpErrorResponse) {
        if (error.status === 403) {
          console.log("Mot de passe invalide");
        } else {
          console.log("Problème de connexion");
        }
      } else {
        // Erreurs Non-HTTP 
        console.log("Problème de connexion");
      }
    }
  }
}
