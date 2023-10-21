import { Component, OnDestroy, OnInit } from "@angular/core";
import { Subscription } from "rxjs";
import { AuthenticationService } from "src/app/login/authentication.service";
import { Message } from "../message.model";
import { MessagesService } from "../messages.service";
import { Router } from "@angular/router";
import { WebSocketService } from "src/environments/websocket.service";
import { WebSocketEvent } from "src/environments/websocket.service";

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

  messages: Message[] = [];
  messagesSubscription: Subscription;

  constructor(
    private router: Router,
    private messagesService: MessagesService,
    private authenticationService: AuthenticationService,
    private webSocketService: WebSocketService
  ) {
    this.usernameSubscription = this.username$.subscribe((u) => {
      this.username = u;
    });
    this.messagesSubscription = this.messages$.subscribe((m) => {
      this.messages = m;
    });
  }

  ngOnInit(): void {
    // Connexion WebSocket
    this.webSocketService
      .connect(this.messages[this.messages.length - 1]?.id)
      .subscribe((event: WebSocketEvent) => {
        if (event === "notif") {
          this.messagesService.fetchMessages().subscribe((messages) => {
            this.messages = messages;
          });
        }
      });
  }

  ngOnDestroy(): void {
    if (this.usernameSubscription) {
      this.usernameSubscription.unsubscribe();
    }
    if (this.messagesSubscription) {
      this.messagesSubscription.unsubscribe();
    }

    //Déconnexion WebSocket
    this.webSocketService.disconnect();
  }

  onPublishMessage(message: string) {
    if (this.username != null) {
      this.messagesService.postMessage({
        text: message,
        username: this.username,
        timestamp: Date.now(),
      });

      /*//Solution temporaire pré-étape 3
      this.messagesService.fetchMessages().subscribe((messages) => {
        this.messages = messages;
      });*/
    }
  }

  onLogout() {
    this.authenticationService.logout();
    this.router.navigate(["/"]);
  }
}
