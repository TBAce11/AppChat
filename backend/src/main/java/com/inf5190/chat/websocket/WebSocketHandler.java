package com.inf5190.chat.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Classe qui gère une connexion websocket.
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final WebSocketManager webSocketManager;

    public WebSocketHandler(WebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Sur une nouvelle connexion, on ajoute la session au websocket manager.
        this.webSocketManager.addSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Sur une déconnexion, on retire la session du websocket manager.
        this.webSocketManager.removeSession(session);
    }

}
