package com.inf5190.chat.websocket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.inf5190.chat.websocket.WebSocketHandler;
import com.inf5190.chat.websocket.WebSocketManager;

/**
 * Classe de configuration pour les websockets.
 */
@Configuration
@EnableWebSocket
@PropertySource("classpath:cors.properties")
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

    private final WebSocketManager webSocketManager;

    public WebSocketConfig(WebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Enregistre le handler pour chaque connexion websocket.
        registry.addHandler(new WebSocketHandler(this.webSocketManager), "/notifications")
                .setAllowedOriginPatterns(this.allowedOrigins.split(","));
    }
}