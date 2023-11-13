import { Component, EventEmitter, Output } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { FileReaderService } from '../file-reader.service'; 
import { ChatImageData } from "../message.model";


@Component({
  selector: "app-new-message-form",
  templateUrl: "./new-message-form.component.html",
  styleUrls: ["./new-message-form.component.css"],
})
export class NewMessageFormComponent {
  messageForm = this.fb.group({
    msg: "",
  });

  file: File | null = null;

  @Output() newMessage = new EventEmitter<{ text: string, imageData: ChatImageData | null }>();

  constructor(
    private fb: FormBuilder,
    private fileReaderService: FileReaderService
  ) {}

  onSendMessage() {
    if (this.messageForm.valid && (this.messageForm.value.msg || this.file)) {
      let imageData: ChatImageData | null = null;

      if (this.file) {
        this.fileReaderService.readFile(this.file).then((chatImageData) => {
          imageData = chatImageData;
          this.file = null;
          this.emitNewMessage(this.messageForm.value.msg || "", imageData);
        });
      } else {
        this.emitNewMessage(this.messageForm.value.msg || "", imageData);
      }

      this.messageForm.reset();
    }
  }

  private emitNewMessage(text: string, imageData: ChatImageData | null) {
    this.newMessage.emit({ text, imageData });
  }

  fileChanged(event: any) {
    this.file = event.target.files[0];
  }
}
