import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { LoginPageComponent } from "./login/login-page/login-page.component";
import { ChatPageComponent } from "./chat/chat-page/chat-page.component";
import { LoginPageGuard } from "./guards/login-page.guard";
import { ChatPageGuard } from './guards/chat-page.guard';

const routes: Routes = [
  { path: 'chat', component: ChatPageComponent, canActivate: [ChatPageGuard] },
  { path: '**', component: LoginPageComponent, canActivate: [LoginPageGuard] },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
