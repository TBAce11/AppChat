import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, tap } from "rxjs";
import { Message } from "./message.model";
import { HttpClient } from "@angular/common/http";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class MessagesService {
  messages = new BehaviorSubject<Message[]>([]);
  messagesPath = "messages";

  constructor(private http: HttpClient) {}

  postMessage(message: Message) {
    console.log(message);
    try {
      this.http
        .post(`${environment.backendUrl}/${this.messagesPath}`, message, {
          withCredentials: true,
        })
        .subscribe((res) => {
          this.messages.next([...this.messages.value, message]);
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
      url += `/${messageId}`;
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
