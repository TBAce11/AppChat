import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, lastValueFrom } from "rxjs";
import { Message, NewMessageRequest } from "./message.model";
import { HttpClient, HttpParams } from "@angular/common/http";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class MessagesService {
  private messages = new BehaviorSubject<Message[]>([]);

  constructor(private httpClient: HttpClient) {}

  async postMessage(message: NewMessageRequest): Promise<Message | undefined> {
    try {
      const result = await lastValueFrom(
        this.httpClient.post<Message>(
          `${environment.backendUrl}/messages`,
          message,
          {
            withCredentials: true,
          }
        )
      );
      return result;
    } catch (error) {
      console.error("Error posting message:", error);
      throw error;
    }
  }

  async fetchMessages(): Promise<void> {
    const lastMessageId =
      this.messages.value.length > 0
        ? this.messages.value[this.messages.value.length - 1]?.id
        : null;

    const isIncrementalFetch = lastMessageId != null;
    let queryParameters = isIncrementalFetch
      ? new HttpParams().set("fromId", lastMessageId)
      : new HttpParams();

    try {
      const messages = await lastValueFrom(
        this.httpClient.get<Message[]>(`${environment.backendUrl}/messages`, {
          params: queryParameters,
          withCredentials: true,
        })
      );

      this.messages.next(
        isIncrementalFetch
          ? [...this.messages.value, ...messages.filter((m) => m != null)]
          : messages.filter((m) => m != null)
      );
    } catch (error) {
      console.error("Error fetching messages:", error);
      throw error;
    }
  }

  getMessages(): Observable<Message[]> {
    return this.messages.asObservable();
  }

  clear() {
    this.messages.next([]);
  }
}
