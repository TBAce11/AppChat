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
    filename: "",
  });

  private file: File | null = null;

  @Output()
  newMessage = new EventEmitter<{ message: string; file: File | null }>();

  constructor(private fb: FormBuilder) {}

  get hasImage() {
    return this.file != null;
  }

  onSendMessage() {
    if (this.messageForm.valid && this.messageForm.value.msg) {
      this.newMessage.emit({
        message: this.messageForm.value.msg,
        file: this.file,
      });
      this.messageForm.reset();
      this.file = null;
    }
  }

  fileChanged(event: any) {
    this.file = event.target.files[0];
  }
}
