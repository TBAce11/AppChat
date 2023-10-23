import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { environment } from "src/environments/environment";

export type WebSocketEvent = string;

@Injectable({
  providedIn: "root",
})
export class WebSocketService {
  private ws: WebSocket | null = null;

  constructor() {}

  public connect(): Observable<WebSocketEvent> {
    this.ws = new WebSocket(`${environment.wsUrl}/notifications`);
    const events = new Subject<WebSocketEvent>();

    this.ws.onmessage = (message) => events.next(message.data);
    this.ws.onclose = () => events.complete();
    this.ws.onerror = () => events.error("error");

    return events.asObservable();
  }

  public disconnect() {
    this.ws?.close();
    this.ws = null;
  }
}
