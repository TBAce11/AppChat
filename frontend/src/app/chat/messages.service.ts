import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, firstValueFrom } from "rxjs";
import { Message, NewMessageRequest } from "./message.model";
import { HttpClient, HttpParams } from "@angular/common/http";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class MessagesService {
  private messages = new BehaviorSubject<Message[]>([]);

  constructor(private httpClient: HttpClient) {}

  async postMessage(message: NewMessageRequest): Promise<Message> {
    return firstValueFrom(
      this.httpClient.post<Message>(
        `${environment.backendUrl}/messages`,
        message,
        {
          withCredentials: true,
        }
      )
    );
  }

  async fetchMessages() {
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
  }

  getMessages(): Observable<Message[]> {
    return this.messages.asObservable();
  }

  clear() {
    this.messages.next([]);
  }
}
