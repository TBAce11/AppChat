package com.inf5190.chat.messages;

import com.inf5190.chat.auth.AuthController;
import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionManager;
import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.model.NewMessageRequest;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Contrôleur qui gère l'API de messages.
 */
@RestController
public class MessageController {
    public static final String MESSAGES_PATH = "/messages";

    private final MessageRepository messageRepository;
    private final WebSocketManager webSocketManager;
    private final SessionManager sessionManager;

    public MessageController(MessageRepository messageRepository,
            WebSocketManager webSocketManager, SessionManager sessionManager) {
        this.messageRepository = messageRepository;
        this.webSocketManager = webSocketManager;
        this.sessionManager = sessionManager;
    }

    @GetMapping(MESSAGES_PATH)
    public List<Message> getMessages(@RequestParam Optional<String> fromId)
            throws InterruptedException, ExecutionException {
        return this.messageRepository.getMessages(fromId.orElse(null));
    }

    @PostMapping(MESSAGES_PATH)
    public Message createMessage(@CookieValue(AuthController.SESSION_ID_COOKIE_NAME) String sessionCookie,
            @RequestBody NewMessageRequest message)
            throws InterruptedException, ExecutionException {
        if (sessionCookie == null || sessionCookie.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        SessionData sessionData = this.sessionManager.getSession(sessionCookie);
        if (sessionData == null || !sessionData.username().equals(message.username())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Message newMessage = this.messageRepository.createMessage(message);

        this.webSocketManager.notifySessions();

        return newMessage;
    }
}
