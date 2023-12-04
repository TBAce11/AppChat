import { Injectable } from "@angular/core";
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpErrorResponse,
} from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { AuthenticationService } from "src/app/login/authentication.service";
import { Router } from "@angular/router";
import { MessagesService } from "../chat/messages.service";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor { //alternative globale incluant ChatPageComponent
  constructor(
    private authenticationService: AuthenticationService,
    private router: Router,
    private messagesService: MessagesService
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 403) {
          this.handle403Error();
        }

        return throwError(() => error);
      })
    );
  }

  private handle403Error(): void {
    this.messagesService.clear();
    this.authenticationService.logout();
    this.router.navigate(["/login"]);
  }
}
