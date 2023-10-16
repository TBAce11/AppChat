import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { Message } from "./message.model";

@Injectable({
  providedIn: "root",
})
export class MessagesService {
  messages = new BehaviorSubject<Message[]>([]);

  constructor() {}

  postMessage(message: Message): void {
    this.messages.next([...this.messages.getValue(), message]);
  }

  getMessages(): Observable<Message[]> {
    return this.messages.asObservable();
  }
}
