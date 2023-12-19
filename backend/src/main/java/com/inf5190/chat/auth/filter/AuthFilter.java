package com.inf5190.chat.auth.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.inf5190.chat.auth.AuthController;
import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionManager;

/**
 * Filtre qui intercepte les requêtes HTTP et valide si elle est autorisée.
 */
public class AuthFilter implements Filter {
    private final SessionManager sessionManager;
    private final List<String> allowedOrigins;

    public AuthFilter(SessionManager sessionManager, List<String> allowedOrigins) {
        this.sessionManager = sessionManager;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader(HttpHeaders.ORIGIN);
        if (this.allowedOrigins.contains(origin)) {
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization");
        }

        // OPTIONS request (pre-flight CORS) => let it pass
        if (httpRequest.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

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
        if (sessionData == null) {
            this.sendAuthErrorResponse(httpRequest, httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }

    protected void sendAuthErrorResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie sessionIdCookie = new Cookie(AuthController.SESSION_ID_COOKIE_NAME, null);
        sessionIdCookie.setPath("/");
        sessionIdCookie.setSecure(true);
        sessionIdCookie.setHttpOnly(true);
        sessionIdCookie.setMaxAge(0);

        response.addCookie(sessionIdCookie);

        String origin = request.getHeader(HttpHeaders.ORIGIN);
        if (this.allowedOrigins.contains(origin)) {
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }

        if (request.getRequestURI().contains(AuthController.AUTH_LOGOUT_PATH)) {
            // Si c'est pour le logout, on retourne simplement 200 OK.
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
