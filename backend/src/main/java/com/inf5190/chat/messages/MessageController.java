package com.inf5190.chat.messages;

import com.inf5190.chat.auth.session.SessionDataAccessor;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;

import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur qui gère l'API de messages.
 */
@RestController
public class MessageController {
    public static final String MESSAGES_PATH = "/messages";

    private MessageRepository messageRepository;
    private WebSocketManager webSocketManager;

    public MessageController(MessageRepository messageRepository,
            WebSocketManager webSocketManager,
            SessionDataAccessor sessionDataAccessor) {
        this.messageRepository = messageRepository;
        this.webSocketManager = webSocketManager;
    }

    // À faire...
}
