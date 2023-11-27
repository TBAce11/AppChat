package com.inf5190.chat.auth.session;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Repository;

/**
 * Classe qui gère les sessions utilisateur.
 * 
 * Pour le moment, on gère en mémoire.
 */
@Repository
public class SessionManager {
    private static final String SECRET_KEY_BASE64 = Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded());
    private static final String JWT_AUDIENCE = "inf5190";

    //private final Map<String, SessionData> sessions = new HashMap<String, SessionData>();

    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public SessionManager() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE64));
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    public String addSession(SessionData authData) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + TimeUnit.HOURS.toMillis(2)); // 2 heures d'expiration

        String compactJws = Jwts.builder()
                .audience().add(JWT_AUDIENCE).and()
                .subject(authData.username())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(this.secretKey)
                .compact();

        return compactJws;
    }

    /*public void removeSession(String sessionId) {
        this.sessions.remove(sessionId);
    }*/

    public SessionData getSession(String token) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String username = claims.getSubject();

            return new SessionData(username);
        } catch (JwtException e) {
            return null; // La session n'existe pas ou le JWT n'est pas valide
        }
    }
}
