import {
  AfterViewChecked,
  Component,
  ElementRef,
  Input,
  ViewChild,
} from "@angular/core";
import { Message } from "../message.model";

@Component({
  selector: "app-messages",
  templateUrl: "./messages.component.html",
  styleUrls: ["./messages.component.css"],
})
export class MessagesComponent implements AfterViewChecked {
  private SCROLL_BOTTOM_TOLERANCE_IN_PX = 20;

  @ViewChild("chatContainer") private chatContainer: ElementRef | undefined =
    undefined;

  // Doit défiler vers le bas lors de la création du composant.
  private shouldScrollToBottomAfterViewChecked = true;

  private _messages: Message[] = [];

  @Input()
  get messages(): Message[] {
    return this._messages;
  }

  set messages(messages: Message[]) {
    if (this.shouldScrollBottom(messages)) {
      this.shouldScrollToBottomAfterViewChecked = true;
    }
    this._messages = messages;
  }

  /** Afficher la date seulement si la date du message précédent est différente du message courant. */
  showDateHeader(messages: Message[] | null, i: number) {
    if (messages != null) {
      if (i === 0) {
        return true;
      } else {
        const prev = new Date(messages[i - 1].timestamp).setHours(0, 0, 0, 0);
        const curr = new Date(messages[i].timestamp).setHours(0, 0, 0, 0);
        return prev != curr;
      }
    }
    return false;
  }

  ngAfterViewChecked() {
    if (this.shouldScrollToBottomAfterViewChecked) {
      this.scrollToBottom();
      this.shouldScrollToBottomAfterViewChecked = false;
    }
  }

  private shouldScrollBottom(messages: Message[]): Boolean {
    return this.newMessageReceived(messages) && this.chatBottomVisible();
  }

  private newMessageReceived(messages: Message[]): Boolean {
    return messages?.length > this._messages.length;
  }

  /**
   * Vérifie si le bas du chat est visible.
   *
   * scrollHeight: hauteur totale en px de l'élément qui affiche le chat
   * scrollTop: hauteur cachée en px de l'élément qui affiche le chat
   * clientHeight: hauteur visible en px de l'élément qui affiche le chat
   *
   * Élement qui affiche le chat (hauteur == scrollHeight)
   *  ------------------------
   *  |                      |    ^
   *  |                      |    |
   *  |                      |  partie cachée (hauteur == scrollTop)
   * +|++++++++++++++++++++++|+ partie visible (hauteur == clientHeight)
   * +|                      |+   |
   * +|                      |+   v
   * +|                      |+
   * +------------------------+
   *
   * Si le bas du chat n'est pas visible c'est que l'utilisateur a intentionnellement
   * fait défiler le chat vers le haut donc on ne veut pas forcer un défilement vers le bas.
   **/
  private chatBottomVisible(): Boolean {
    return (
      !!this.chatContainer &&
      this.chatContainer.nativeElement.scrollHeight -
        (this.chatContainer.nativeElement.scrollTop +
          this.chatContainer.nativeElement.clientHeight) <=
        this.SCROLL_BOTTOM_TOLERANCE_IN_PX
    );
  }

  private scrollToBottom(): void {
    if (this.chatContainer != null) {
      this.chatContainer.nativeElement.scrollTop =
        this.chatContainer.nativeElement.scrollHeight;
    }
  }
}
