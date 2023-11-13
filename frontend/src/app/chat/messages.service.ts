import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, tap } from "rxjs";
import { Message, NewMessageRequest } from "./message.model";
import { HttpClient } from "@angular/common/http";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class MessagesService {
  messages = new BehaviorSubject<Message[]>([]);
  messagesPath = "messages";

  constructor(private http: HttpClient) {}

  postMessage(newMessage: NewMessageRequest): void {
    console.log(newMessage);
    try {
      const message: Message = {
        id: "",
        text: newMessage.text,
        username: newMessage.username,
        timestamp: Date.now(),
        imageUrl: null,
      };

      this.http
        .post<Message>(
          `${environment.backendUrl}/${this.messagesPath}`,
          message,
          {
            withCredentials: true,
          }
        )
        .subscribe((res) => {
          // this.messages.next([...this.messages.value, res]);
        });
    } catch (err) {
      throw new Error("Une erreur est survenue en envoyant le message.");
    }
  }

  getMessages(): Observable<Message[]> {
    return this.messages.asObservable();
  }

  fetchMessages(messageId?: string): void {
    console.log(messageId);
    let url = `${environment.backendUrl}/${this.messagesPath}`;
    if (messageId) {
      url += `?fromId=${messageId}`;
    }
    this.http
      .get<Message[]>(url, {
        withCredentials: true,
      })
      .subscribe((messages) => {
        this.messages.next(messages);
      });
  }
}
