package com.inf5190.chat.messages.model;

public record NewMessageRequest(String text, String username, ChatImageData imageData) {}
