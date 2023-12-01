import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { LoginPageComponent } from "./login/login-page/login-page.component";
import { ChatPageComponent } from "./chat/chat-page/chat-page.component";
import { loginPageGuard } from "./guards/login-page.guard";
import { chatPageGuard } from "./guards/chat-page.guard";

const routes: Routes = [
  { path: "chat", component: ChatPageComponent, canActivate: [chatPageGuard] },
  { path: "**", component: LoginPageComponent, canActivate: [loginPageGuard] },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
