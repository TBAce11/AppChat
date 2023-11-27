import { Component, OnInit } from "@angular/core";
import { UserCredentials } from "../model/user-credentials";
import { AuthenticationService } from "../authentication.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-login-page",
  templateUrl: "./login-page.component.html",
  styleUrls: ["./login-page.component.css"],
})
export class LoginPageComponent implements OnInit {
  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  async onLogin(userCredentials: UserCredentials) {
    await this.authenticationService.login(userCredentials);
    this.router.navigate(["/chat"]);
  }
}
