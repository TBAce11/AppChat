package com.inf5190.chat.messages.model;

public record Message(String id, String username, Long timestamp, String text, String imageUrl) {
}