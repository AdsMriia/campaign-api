package com.example.security.jwt;

import java.util.UUID;

import javax.crypto.SecretKey;

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
    public String getRoleIdFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    public WebUserDto validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            WebUserDto userDto = new WebUserDto();
            userDto.setEmail(claims.getSubject());
            userDto.setId(UUID.fromString(claims.get("userId", String.class)));
            //    userDto.setWorkspaceId(UUID.fromString(claims.get("workspace_id", String.class)));
//            userDto.setIsActive(true);
            userDto.setToken(token);

            return userDto;
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
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
