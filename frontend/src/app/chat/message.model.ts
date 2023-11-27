export interface Message {
  id: string;
  text: string;
  username: string;
  timestamp: number;
  imageUrl: string | null;
}

export interface NewMessageRequest {
  text: string;
  username: string;
  imageData: ChatImageData | null;
}

export interface ChatImageData {
  data: string;
  type: string;
}
