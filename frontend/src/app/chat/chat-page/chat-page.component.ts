import { Component, OnDestroy, OnInit } from "@angular/core";
import { Observable, Subscription } from "rxjs";
import { AuthenticationService } from "src/app/login/authentication.service";
import { MessagesService } from "../messages.service";
import { Router } from "@angular/router";
import { WebSocketEvent, WebSocketService } from "../websocket.service";
import { FileReaderService } from "../file-reader.service";
import { interval } from "rxjs";
import { switchMap } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";

@Component({
  selector: "app-chat-page",
  templateUrl: "./chat-page.component.html",
  styleUrls: ["./chat-page.component.css"],
})
export class ChatPageComponent implements OnInit, OnDestroy {
  messages$ = this.messagesService.getMessages();
  username$ = this.authenticationService.getUsername();

  username: string | null = null;
  usernameSubscription: Subscription;

  notifications$: Observable<WebSocketEvent> | null = null;
  notificationsSubscription: Subscription | null = null;

  constructor(
    private router: Router,
    private messagesService: MessagesService,
    private authenticationService: AuthenticationService,
    private webSocketService: WebSocketService,
    private fileReaderService: FileReaderService
  ) {
    this.usernameSubscription = this.username$.subscribe((u) => {
      this.username = u;
    });
  }

  ngOnInit() {
    this.notifications$ = this.webSocketService.connect();
    this.notificationsSubscription = this.notifications$.subscribe(() => {
      this.fetchMessages();
    });
    this.fetchMessages();
  }

  ngOnDestroy(): void {
    if (this.usernameSubscription) {
      this.usernameSubscription.unsubscribe();
    }
    if (this.notificationsSubscription) {
      this.notificationsSubscription.unsubscribe();
    }
    this.webSocketService.disconnect();
  }

  async onPublishMessage(event: { message: string; file: File | null }) {
    if (this.username != null) {
      const imageData =
        event.file != null
          ? await this.fileReaderService.readFile(event.file)
          : null;

      try {
        await this.messagesService.postMessage({
          text: event.message,
          username: this.username,
          imageData: imageData,
        });
      } catch (error) {
        if (error instanceof HttpErrorResponse && error.status === 403) {
          await this.onLogout();
        } else {
          console.error("Impossible d'envoyer le message.");
        }
      }
    }
  }

  async onLogout() {
    try {
      await this.authenticationService.logout();
    } finally {
      this.messagesService.clear();
      this.router.navigate(["/"]);
    }
  }

  private async fetchMessages() {
    try {
      await this.messagesService.fetchMessages();
    } catch (error) {
      if (error instanceof HttpErrorResponse && error.status === 403) {
        await this.onLogout();
      } else {
        console.error("Impossible de charger les messages.");
      }
    }
  }
}
