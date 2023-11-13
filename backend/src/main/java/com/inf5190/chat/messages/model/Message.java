package com.inf5190.chat.messages.model;

public record Message(String id, String text, String username, Long timestamp, String imageURL) {}