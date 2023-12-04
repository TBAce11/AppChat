import { Component, EventEmitter, OnInit, Output } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { UserCredentials } from "../model/user-credentials";
import { HttpErrorResponse } from "@angular/common/http";

@Component({
  selector: "app-login-form",
  templateUrl: "./login-form.component.html",
  styleUrls: ["./login-form.component.css"],
})
export class LoginFormComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage: string = "";

  @Output()
  login = new EventEmitter<UserCredentials>();

  constructor(private fb: FormBuilder) {
    this.loginForm = this.fb.group({
      username: ["", [Validators.required]],
      password: ["", [Validators.required]],
    });
  }

  get username() {
    return this.loginForm.get("username");
  }

  get password() {
    return this.loginForm.get("password");
  }

  ngOnInit(): void {}

  onLogin() {
    if (this.loginForm.valid) {
      this.errorMessage = ""; // Réinitialisation du message d'erreur
      this.login.emit({
        username: this.loginForm.value.username,
        password: this.loginForm.value.password,
      });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }

  handleLoginError(error: any) {
    console.log("Erreur détectée:", error);
    if (error instanceof HttpErrorResponse) {
      if (error.status === 403) {
        this.errorMessage = "Mot de passe invalide";
      } else {
        this.errorMessage = "Problème de connexion";
      }
    } else {
      this.errorMessage = "Problème de connexion";
    }
  }
}
