package com.tevind.whispr.security.jwt;

import com.tevind.whispr.enums.AccountRoles;
import com.tevind.whispr.exception.JwtTokenErrorException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey key;

    @PostConstruct
    private void init() {
        log.debug("Initializing JWT signing key");
        this.key = getKey(secret);
    }

    private SecretKey getKey(String secret) {
        log.debug("Getting signing key");
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private String createToken(Map<String, Object> claims, String username) {
        try {
            log.debug("Creating token for user: {}", username);
            String token = Jwts.builder()
                    .claims(claims)
                    .subject(username)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(key)
                    .compact();

            log.debug("Generated token: {}", token);
            return token;
        } catch (Exception err) {
            log.error("Error creating JWT Token for user: {}", username);
            throw new JwtTokenErrorException("Error creating token for user: " + username);
        }
    }

    public String generateToken(String username, UUID userId, Set<AccountRoles> roles) {
        log.debug("Generating token for User: {}", username);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("roles", roles.toString());

        return createToken(claims, username);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date getExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public UUID getUserId(String token) {
        String userId = extractClaim(token, claims -> claims.get("userId", String.class));
        return UUID.fromString(userId);
    }

    public Date getIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public Boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsername(token);
        return !isTokenExpired(token) && tokenUsername.equals(username);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        try {
            log.debug("Processing token from request");
            String token = request.getHeader("Authentication");

            log.debug("Token pulled from Authentication header: {}", token);

            if (!token.startsWith("Bearer ")) {
                log.warn("Token is not a Bearer token: {}", token);
                throw new JwtTokenErrorException("Token is not a Bearer token");
            }

            log.debug("Valid bearer token");
            token = token.substring(7);

            log.debug("Token from request: {}", token);
            return token;

        } catch (Exception err) {
            log.error("Error processing token from Authentication Header");
            throw new JwtTokenErrorException(err.getMessage());
        }
    }
}
