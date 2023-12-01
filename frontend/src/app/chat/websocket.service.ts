import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { environment } from "src/environments/environment";

export type WebSocketEvent = "notif";

@Injectable({
  providedIn: "root",
})
export class WebSocketService {
  private ws: WebSocket | null = null;
  private retryInterval = 2000;
  private retryAttempts = 10;

  private events = new Subject<WebSocketEvent>();

  constructor() {}

  public connect(): Observable<WebSocketEvent> {
    this.retryConnection();
    return this.events.asObservable();
  }

  private retryConnection(attempt = 1) {
    this.ws = new WebSocket(`${environment.wsUrl}/notifications`);

    this.ws.onopen = () => {
      attempt = 1;
      console.log("Connexion établie");

      // Notification de connexion établie
      this.events.next("notif");
    };

    this.ws.onmessage = () => this.events.next("notif");

    this.ws.onclose = () => {
      if (attempt <= this.retryAttempts) {
        console.log("Nouvelle tentative de connexion");
        // Nouvelle tentative de connexion après l'intervalle spécifiée
        setTimeout(() => this.retryConnection(attempt + 1), this.retryInterval);
      } else {
        // Maximum de tentatives atteint -> arrêt des tentatives et notification de l'erreur
        console.log("Maximum de tentatives atteint");
        this.events.complete();
      }
    };
    this.ws.onerror = () => {
      console.log("Erreur: perte de connexion en cours");
      this.events.error("error");
    };
  }

  public disconnect() {
    this.ws?.close();
    this.ws = null;
  }
}
