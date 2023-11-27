package com.inf5190.chat.messages.model;

public record NewMessageRequest(String username, String text, ChatImageData imageData) {}

