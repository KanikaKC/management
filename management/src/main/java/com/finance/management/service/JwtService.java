package com.finance.management.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {
    private String SECRET_KEY = "d30088c7eba64217040fb39f5dab270124004cc403e75c088eae575214afa7dd";
    private long VALIDITY = 1000 * 60 * 6;
    private long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 6;

    public String generateToken(String emailId) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, emailId);
    }

    private String createToken(Map<String, Object> claims, String emailId) {
        try {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(emailId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + VALIDITY))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    } catch (JwtException e) {
            return "Invalid token";
        }
    }

    public boolean validateToken(String token, String userEmail) {
        try {
            String extractedEmail = extractUsername(token);
            return (extractedEmail.equals(userEmail) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }


    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}

