import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { Message } from "./message.model";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class MessagesService {
  messages = new BehaviorSubject<Message[]>([]);
  messagesPath = "/messages";

  constructor(private http: HttpClient) {}

  postMessage(message: Message): void {
    /*return this.http.post<Message>(this.messagesURL, message).pipe(
      tap((newMessage) => {
        this.messages.next([...this.messages.getValue(), newMessage]);
      })
    );*/
    this.messages.next([...this.messages.value, message]);
  }

  getMessages(): Observable<Message[]> {
    return this.messages.asObservable();
  }

  fetchMessages(): Observable<Message[]> {
    /*this.http.get<Message[]>(this.messagesURL).subscribe((messages) => {
      this.messages.next(messages);
    });*/
    return this.http.get<Message[]>(this.messagesPath);
  }
}
