package com.example.security.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.exception.TokenValidationException;
import com.example.model.dto.WebUserDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    //    public String generateToken(WebUserDto user) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpiration);
//
//        return Jwts.builder()
//                .setSubject(user.getEmail())
//                .claim("user_id", user.getId().toString())
//                .claim("roles", user.getRoles())
//                .claim("workspace_id", user.getWorkspaceId())
//                .claim("tokenType", "access")
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(getSigningKey())
//                .compact();
//    }
    public String getRoleFromToken(String token) {
        Claims claims = extractAllClaims(token);

        return claims.get("role", String.class);
    }

    public WebUserDto validateToken(String token) {
        Claims claims = extractAllClaims(token);

        WebUserDto userDto = new WebUserDto();
        userDto.setEmail(claims.getSubject());
        userDto.setId(UUID.fromString(claims.get("userId", String.class)));
        //    userDto.setWorkspaceId(UUID.fromString(claims.get("workspace_id", String.class)));
//            userDto.setIsActive(true);
        userDto.setToken(token);

        return userDto;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateApiToken() {
        Date date = Date.from(LocalDateTime.now().plusSeconds(10).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject("campaign-api")
                .expiration(date)
                .signWith(getSigningKey())
                .claims(
                        Map.of(
                                "tokenType", "api-service"
                        ))
                .compact();
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException ex) {
            throw new TokenValidationException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            throw new TokenValidationException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new TokenValidationException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new TokenValidationException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new TokenValidationException("JWT claims string is empty");
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw new TokenValidationException("JWT token is invalid");
        }
    }

    public String getTokenType(String jwt) {
        Claims claims = extractAllClaims(jwt);
        return claims.get("tokenType", String.class);
    }

    public UUID getUserIdFromToken(String jwt) {
        Claims claims = extractAllClaims(jwt);
        return UUID.fromString(claims.get("userId", String.class));
    }

    public String getEmailFromToken(String jwt) {
        Claims claims = extractAllClaims(jwt);
        return claims.getSubject();
    }
}
