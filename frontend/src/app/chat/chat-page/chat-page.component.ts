import { Component, OnDestroy, OnInit } from "@angular/core";
import { Subscription } from "rxjs";
import { AuthenticationService } from "src/app/login/authentication.service";
import { ChatImageData, Message, NewMessageRequest } from "../message.model";
import { MessagesService } from "../messages.service";
import { Router } from "@angular/router";
import { WebSocketService } from "src/environments/websocket.service";
import { WebSocketEvent } from "src/environments/websocket.service";
import { FileReaderService } from "../file-reader.service"; // Update with the correct path

const regex = /notif:(.*)/;

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

  file: File | null = null;

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
    this.messagesSubscription = this.messages$.subscribe((m) => {
      console.log("m", m);
      console.log("this.messages", this.messages);
      for (let msg in m) {
        this.messages.push(m[msg]);
      }
    });
  }

  getNotificationId(message: string): string | undefined {
    const matches = message.match(regex);
    if (matches) {
      return matches[1];
    }
    console.error("No notification ID found in message", message);
    return undefined;
  }

  ngOnInit(): void {
    // Connexion WebSocket
    this.webSocketService.connect().subscribe((event: WebSocketEvent) => {
      if (event.startsWith("notif")) {
        this.messagesService.fetchMessages(this.getNotificationId(event));
      } else {
        this.messagesService.fetchMessages(); 
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

  onPublishMessage(message: string | null) {
    if (message !== null) {
      let imageData: ChatImageData | null = null;

      if (this.file) {
        this.fileReaderService.readFile(this.file).then((chatImageData) => {
          imageData = chatImageData;
          this.file = null; // Nettoyage après lecture
          this.postMessageWithImage(message, imageData);
        });
      } else {
        this.postMessageWithImage(message, imageData);
      }
    }
  }

  private postMessageWithImage(message: string, imageData: ChatImageData | null) {
    if (this.username !== null) {
      const newMessage: NewMessageRequest = {
        text: message,
        username: this.username,
        imageData: imageData,
      };

      this.messagesService.postMessage(newMessage);
    }
  }

  onLogout() {
    this.authenticationService.logout();
    this.router.navigate(["/"]);
  }
}
