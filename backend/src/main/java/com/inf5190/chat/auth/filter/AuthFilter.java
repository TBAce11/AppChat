package com.inf5190.chat.auth.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;

import com.inf5190.chat.auth.AuthController;
import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionDataAccessor;
import com.inf5190.chat.auth.session.SessionManager;

/**
 * Filtre qui intercepte les requêtes HTTP et valide si elle est autorisée.
 */
public class AuthFilter implements Filter {
    private final SessionDataAccessor sessionDataAccessor;
    private final SessionManager sessionManager;

    public AuthFilter(SessionDataAccessor sessionDataAccessor, SessionManager sessionManager) {
        this.sessionDataAccessor = sessionDataAccessor;
        this.sessionManager = sessionManager;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Si c'est la méthode OPTIONS on laisse passer. C'est une requête
        // pre-flight pour les CORS.
        if (httpRequest.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
            chain.doFilter(request, response);
            return;
        }

        // On vérifie si le session cookie est présent sinon on n'accepte pas la
        // requête.
        final Cookie[] cookies = httpRequest.getCookies();

        if (cookies == null) {
            this.sendAuthErrorResponse(httpRequest, httpResponse);
            return;
        }

        final Optional<Cookie> sessionCookie = Arrays.stream(cookies)
                .filter(c -> c.getName() != null && c.getName().equals(AuthController.SESSION_ID_COOKIE_NAME))
                .findFirst();
        if (sessionCookie.isEmpty()) {
            this.sendAuthErrorResponse(httpRequest, httpResponse);
            return;
        }

        SessionData sessionData = this.sessionManager.getSession(sessionCookie.get().getValue());

        // On vérifie si la session existe sinon on n'accepte pas la requête.
        if (sessionData == null) {
            this.sendAuthErrorResponse(httpRequest, httpResponse);
            return;
        }

        this.sessionDataAccessor.setSessionData(httpRequest, sessionData);

        chain.doFilter(request, response);
    }

    protected void sendAuthErrorResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie sessionIdCookie = new Cookie(AuthController.SESSION_ID_COOKIE_NAME, null);
        sessionIdCookie.setPath("/");
        sessionIdCookie.setSecure(true);
        sessionIdCookie.setHttpOnly(true);
        sessionIdCookie.setMaxAge(0);

        response.addCookie(sessionIdCookie);

        if (request.getRequestURI().contains(AuthController.AUTH_LOGOUT_PATH)) {
            // Si c'est pour le logout, on retourne simplement 200 OK.
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
