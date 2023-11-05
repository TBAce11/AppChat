package com.inf5190.chat.auth;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.Cookie;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionManager;
import com.inf5190.chat.auth.repository.UserAccountRepository;
import com.inf5190.chat.auth.repository.FirestoreUserAccount;

/**
 * Contrôleur qui gère l'API de login et logout.
 */
@RestController()
public class AuthController {
    public static final String AUTH_LOGIN_PATH = "/auth/login";
    public static final String AUTH_LOGOUT_PATH = "/auth/logout";
    public static final String SESSION_ID_COOKIE_NAME = "sid";

    private final SessionManager sessionManager;
    private final UserAccountRepository userAccountRepository;

    public AuthController(SessionManager sessionManager, UserAccountRepository userAccountRepository) {
        this.sessionManager = sessionManager;
        this.userAccountRepository = userAccountRepository;
    }

    @PostMapping(AUTH_LOGIN_PATH)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest)
            throws InterruptedException, ExecutionException {

        String username = loginRequest.username();

        FirestoreUserAccount existingAccount = userAccountRepository.getUserAccount(username);

        if(existingAccount == null) {
            existingAccount = new FirestoreUserAccount(username, loginRequest.password());
            userAccountRepository.setUserAccount(existingAccount);
        }

        SessionData sessionData = new SessionData(loginRequest.username());

        String sessionId = sessionManager.addSession(sessionData);

        ResponseCookie cookie = ResponseCookie.from(SESSION_ID_COOKIE_NAME, sessionId)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofHours(24))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        LoginResponse response = new LoginResponse(loginRequest.username());

        return ResponseEntity.ok().headers(headers).body(response);
    }

    @PostMapping(AUTH_LOGOUT_PATH)
    public ResponseEntity<Void> logout(@CookieValue("sid") Cookie sessionCookie) {
        String sessionId = sessionCookie.getValue();

        sessionManager.removeSession(sessionId);

        ResponseCookie cookie = ResponseCookie.from(SESSION_ID_COOKIE_NAME, null) // ou ""
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().headers(headers).build();
    }
}
