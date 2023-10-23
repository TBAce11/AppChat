package com.inf5190.chat.websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

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
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
    }

    /**
     * Fonction pour envoyer une notification à toutes les sessions websocket actives.
     * https://stackoverflow.com/questions/29002063/websocket-the-remote-endpoint-was-in-state-text-partial-writing
     */
    public void notifySessions(Long messageId) {
        for (WebSocketSession wss : sessions.values()) {
            try {
                synchronized (wss) {
                    wss.sendMessage(new TextMessage("notif:" + messageId));
                }
            } catch (IOException e) {
                logger.error("Could not notify session.", e);
            }
        }
    }
}
