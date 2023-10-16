package com.inf5190.chat.websocket;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Service qui gère les sessions websocket.
 */
@Service
public class WebSocketManager {
    private final Logger logger = LoggerFactory.getLogger(WebSocketManager.class);
    private final LinkedHashMap<String, WebSocketSession> sessions = new LinkedHashMap<String, WebSocketSession>();

    public void addSession(WebSocketSession session) {
        this.sessions.put(session.getId(), session);
    }

    public void removeSession(WebSocketSession session) {
        this.sessions.remove(session.getId());
    }

    /**
     * Fonction pour envoyer une notification à toutes les sessions websocket actives.
     */
    public void notifySessions() {
        for (WebSocketSession s : sessions.values()) {
            try {
                s.sendMessage(new TextMessage("notif"));
            } catch (IOException e) {
                logger.info("Could not notify session.", e);
            }
        }
    }

}
