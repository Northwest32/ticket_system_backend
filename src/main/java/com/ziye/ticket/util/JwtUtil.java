package com.ziye.ticket.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {
    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 1 day
    private final String SECRET = "your-256-bit-secret-your-256-bit-secret"; 
    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    private Key getKey() {
        return new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), SIGNATURE_ALGORITHM.getJcaName());
    }

    public String generateToken(Long userId, String userType, String username) {
        // ensure userType is lowercase to match frontend expected format
        String normalizedUserType = userType != null ? userType.toLowerCase() : "buyer";
        System.out.println("✅ Generating token for: id=" + userId + ", username=" + username + ", userType=" + normalizedUserType);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("userType", normalizedUserType)
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SIGNATURE_ALGORITHM, getKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .parseClaimsJws(token)
                .getBody();
    }
} 