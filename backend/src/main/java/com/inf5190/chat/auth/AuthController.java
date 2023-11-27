package com.inf5190.chat.auth;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.auth.repository.FirestoreUserAccount;
import com.inf5190.chat.auth.repository.UserAccountRepository;
import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionManager;

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
    private final PasswordEncoder passwordEncoder;

    public AuthController(SessionManager sessionManager, UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder) {
        this.sessionManager = sessionManager;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(AUTH_LOGIN_PATH)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest)
            throws InterruptedException, ExecutionException {
        FirestoreUserAccount account = this.userAccountRepository.getUserAccount(loginRequest.username());
        if (account == null) {
            String encodedPassword = this.passwordEncoder.encode(loginRequest.password());
            this.userAccountRepository
                    .createUserAccount(new FirestoreUserAccount(loginRequest.username(), encodedPassword));
        } else if (!this.passwordEncoder.matches(loginRequest.password(), account.getEncodedPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String sessionId = this.sessionManager.addSession(new SessionData(loginRequest.username()));

        ResponseCookie sessionCookie = this.createResponseSessionCookie(sessionId, TimeUnit.DAYS.toSeconds(1));

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .body(new LoginResponse(loginRequest.username()));
    }

    @PostMapping(AUTH_LOGOUT_PATH)
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteSessionCookie = this.createResponseSessionCookie(null, 0);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSessionCookie.toString()).body(null);

    }

    private ResponseCookie createResponseSessionCookie(String sessiondId, long maxAge) {
        return ResponseCookie.from(SESSION_ID_COOKIE_NAME, sessiondId)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(maxAge)
                .build();
    }
}
