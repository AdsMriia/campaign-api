package com.example.security.jwt;

import com.example.exception.TokenValidationException;
import com.example.model.dto.WebUserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.jar.asm.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(WebUserDto user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles())
                .claim("workspace_id", user.getWorkspaceId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public WebUserDto validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            WebUserDto userDto = new WebUserDto();
            userDto.setId(UUID.fromString(claims.getSubject()));
            userDto.setEmail(claims.get("email", String.class));
            userDto.setRoles(claims.get("roles", List.class));
            userDto.setWorkspaceId(UUID.fromString(claims.get("workspace_id", String.class)));
            userDto.setIsActive(true);

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
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
