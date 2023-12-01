import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, firstValueFrom, throwError } from "rxjs";
import { Message, NewMessageRequest } from "./message.model";
import {
  HttpClient,
  HttpParams,
  HttpErrorResponse,
} from "@angular/common/http";
import { environment } from "src/environments/environment";
import { AuthenticationService } from "../login/authentication.service";
import { Router } from "@angular/router";

@Injectable({
  providedIn: "root",
})
export class MessagesService {
  private messages = new BehaviorSubject<Message[]>([]);

  constructor(
    private httpClient: HttpClient,
    private router: Router,
    private authenticationService: AuthenticationService
  ) {}

  async postMessage(message: NewMessageRequest): Promise<Message> {
    try {
      return firstValueFrom(
        this.httpClient.post<Message>(
          `${environment.backendUrl}/messages`,
          message,
          {
            withCredentials: true,
          }
        )
      );
    } catch (error) {
      this.handleHttpError(error);
      throw error;
    }
  }

  async fetchMessages() {
    try {
      const lastMessageId =
        this.messages.value.length > 0
          ? this.messages.value[this.messages.value.length - 1].id
          : null;

      const isIncrementalFetch = lastMessageId != null;
      let queryParameters = isIncrementalFetch
        ? new HttpParams().set("fromId", lastMessageId)
        : new HttpParams();

      const messages = await firstValueFrom(
        this.httpClient.get<Message[]>(`${environment.backendUrl}/messages`, {
          params: queryParameters,
          withCredentials: true,
        })
      );
      this.messages.next(
        isIncrementalFetch ? [...this.messages.value, ...messages] : messages
      );
    } catch (error) {
      this.handleHttpError(error);
      throw error;
    }
  }

  private handleHttpError(error: any): void {
    if (error instanceof HttpErrorResponse && error.status === 403) {
      console.error("Erreur HTTP 403. Déconnexion en cours...");

      this.clear();
      this.authenticationService.logout();
      this.router.navigate(["/login"]);
    }
  }

  getMessages(): Observable<Message[]> {
    return this.messages.asObservable();
  }

  clear() {
    this.messages.next([]);
  }
}
