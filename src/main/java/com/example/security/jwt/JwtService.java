package com.example.security.jwt;

import com.telegram.exception.token.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class JwtService {

    private final static Logger LOGGER = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    public boolean verifyToken(String token) {
        try {
            if (getTokenType(token).equals("access")) {
                return true;
            }
            throw new InvalidTokenException("Invalid access token");
        } catch (ExpiredJwtException expEx) {
            throw new TokenExpiredException("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            throw new UnsupportedTokenException("Unsupported token");
        } catch (MalformedJwtException mjEx) {
            throw new MalformedTokenException("Malformed token");
        } catch (SecurityException sEx) {
            throw new InvalidTokenSignatureException("Invalid signature");
        } catch (TokenDeprecatedException tdEx) {
            throw tdEx;
        } catch (Exception e) {
            LoggerFactory.getLogger(JwtService.class).error(e.getMessage());
            LoggerFactory.getLogger(JwtService.class).error(String.join("\n", Arrays.toString(e.getStackTrace())));
            throw new InvalidTokenException("Unknown error");
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getTokenType(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("tokenType", String.class);
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UUID.fromString(claims.get("userId", String.class));
    }

    public String generateApiToken() {
        Date date = Date.from(LocalDateTime.now().plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject("Telegram-api")
                .expiration(date)
                .signWith(getSignInKey())
                .claims(
                        Map.of(
                                "tokenType", "access"
                        ))
                .compact();
    }
}
