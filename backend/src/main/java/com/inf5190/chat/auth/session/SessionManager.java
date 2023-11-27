package com.inf5190.chat.auth.session;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Repository;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

/**
 * Classe qui gère les sessions utilisateur.
 * 
 * Pour le moment, on gère en mémoire.
 */
@Repository
public class SessionManager {
    private static final String SECRET_KEY_BASE64 = Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded());
    private static final String JWT_AUDIENCE = "inf5190";
    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public SessionManager() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE64));
        this.jwtParser = Jwts.parser().verifyWith(this.secretKey).build();
    }

    public String addSession(SessionData authData) {
        Date now = new Date();

        String compactJws = Jwts.builder()
                .audience().add(JWT_AUDIENCE).and()
                .subject(authData.username())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + TimeUnit.HOURS.toMillis(2)))
                .signWith(this.secretKey)
                .compact();

        return compactJws;
    }

    public SessionData getSession(String sessionId) {
        try {
            return new SessionData(this.jwtParser.parseSignedClaims(sessionId).getPayload().getSubject());
        } catch (JwtException e) {
            return null;
        }
    }

}
