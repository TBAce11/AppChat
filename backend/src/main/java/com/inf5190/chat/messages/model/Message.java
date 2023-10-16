package com.inf5190.chat.messages.model;

/**
 * Représente un message.
 */
public record Message(Long id, String username, Long timestamp, String text) {
}
