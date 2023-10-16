package com.inf5190.chat.auth.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

/**
 * Classe qui gère les sessions utilisateur.
 * 
 * Pour le moment, on gère en mémoire.
 */
@Repository
public class SessionManager {

    private final Map<String, SessionData> sessions = new HashMap<String, SessionData>();

    public String addSession(SessionData authData) {
        final String sessionId = this.generateSessionId();
        this.sessions.put(sessionId, authData);
        return sessionId;
    }

    public void removeSession(String sessionId) {
        this.sessions.remove(sessionId);
    }

    public SessionData getSession(String sessionId) {
        return this.sessions.get(sessionId);
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
