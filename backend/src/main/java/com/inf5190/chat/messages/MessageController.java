package com.inf5190.chat.messages;

import com.inf5190.chat.auth.session.SessionDataAccessor;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;
import com.inf5190.chat.messages.model.Message;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur qui gère l'API de messages.
 */
@RestController
public class MessageController {
    public static final String MESSAGES_PATH = "/messages";
    public static final String MESSAGES_PATH_FILTERED = "/messages/{fromId}";

    private MessageRepository messageRepository;
    private WebSocketManager webSocketManager;

    public MessageController(MessageRepository messageRepository,
            WebSocketManager webSocketManager,
            SessionDataAccessor sessionDataAccessor) {
        this.messageRepository = messageRepository;
        this.webSocketManager = webSocketManager;
    }

    @GetMapping(MESSAGES_PATH)
    public List<Message> getMessages(@RequestParam(value = "fromId", required = false) String fromId) {
        return messageRepository.getMessages(fromId);
    }

    @PostMapping(MESSAGES_PATH)
    public Message createMessage(@RequestBody Message message) {
        Message newMessage = messageRepository.createMessage(message);
        webSocketManager.notifySessions(newMessage.id());
        return newMessage;
    }
}
