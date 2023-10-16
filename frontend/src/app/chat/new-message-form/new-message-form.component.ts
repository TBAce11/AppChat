import { Component, EventEmitter, Output } from "@angular/core";
import { FormBuilder } from "@angular/forms";

@Component({
  selector: "app-new-message-form",
  templateUrl: "./new-message-form.component.html",
  styleUrls: ["./new-message-form.component.css"],
})
export class NewMessageFormComponent {
  messageForm = this.fb.group({
    msg: "",
  });

  @Output()
  newMessage = new EventEmitter<string>();

  constructor(private fb: FormBuilder) {}

  onSendMessage() {
    if (this.messageForm.valid && this.messageForm.value.msg) {
      this.newMessage.emit(this.messageForm.value.msg);
      this.messageForm.reset();
    }
  }
}
