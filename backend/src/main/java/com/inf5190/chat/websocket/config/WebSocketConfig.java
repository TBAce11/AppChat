package com.inf5190.chat.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.inf5190.chat.websocket.WebSocketHandler;
import com.inf5190.chat.websocket.WebSocketManager;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketManager webSocketManager;

    @Autowired
    @Qualifier("allowedOrigins")
    private String[] allowedOrigins;

    public WebSocketConfig(WebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(this.webSocketManager), "/notifications")
                .setAllowedOriginPatterns(allowedOrigins);
    }
}