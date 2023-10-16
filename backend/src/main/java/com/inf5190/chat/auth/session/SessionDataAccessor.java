package com.inf5190.chat.auth.session;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

/**
 * Classe utilitaire qui permet de récupérer les informations du contexte de la
 * requête.
 */
@Component
public class SessionDataAccessor {
    private static final String SESSION_DATA_KEY = "SESSION_DATA_KEY";

    public void setSessionData(HttpServletRequest request, SessionData sessionData) {
        request.setAttribute(SESSION_DATA_KEY, sessionData);
    }

    public SessionData getSessionData(HttpServletRequest request) {
        return (SessionData) request.getAttribute(SESSION_DATA_KEY);
    }
}
