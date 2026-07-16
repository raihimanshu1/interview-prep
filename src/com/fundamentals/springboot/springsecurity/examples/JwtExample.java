package com.fundamentals.springboot.springsecurity.examples;

//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Companion code for Spring Security deep-dive.
 * Demonstrates JWT creation, validation, and extraction.
 * Run: javac JwtExample.java && java JwtExample
 */

public class JwtExample {
    private static final String SECRET = Base64.getEncoder()
        .encodeToString("my-production-secret-key-change-this!".getBytes());
    private static final long EXPIRY_MS = 3600000; // 1 hour

    public static void main(String[] args) {
        // 1. Create JWT token
        String token = createToken("user-123", "ADMIN");
        System.out.println("Generated JWT:\n" + token);
        System.out.println();

        // 2. Validate token
        boolean valid = validateToken(token);
        System.out.println("Token valid: " + valid);

        // 3. Extract claims
        String userId = getUserId(token);
        String role = getRole(token);
        System.out.println("User ID: " + userId);
        System.out.println("Role: " + role);
        System.out.println();

        // 4. Tampered token should fail validation
        String tampered = token + "x";
        System.out.println("Tampered token valid: " + validateToken(tampered));

        // 5. Expired token should fail
        String expiredToken = createExpiredToken("user-456", "USER");
        System.out.println("Expired token valid: " + validateToken(expiredToken));

        // 6. Show decoded header + payload (without verification)
        showDecodedParts(token);
    }

    public static String createToken(String userId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRY_MS);

        return Jwts.builder()
            .setSubject(userId)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static String getUserId(String token) {
        return Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public static String getRole(String token) {
        return Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody()
            .get("role", String.class);
    }

    private static String createExpiredToken(String userId, String role) {
        Date now = new Date();
        Date past = new Date(now.getTime() - EXPIRY_MS); // Already expired
        
        return Jwts.builder()
            .setSubject(userId)
            .claim("role", role)
            .setIssuedAt(past)
            .setExpiration(past)
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
    }

    private static void showDecodedParts(String token) {
        String[] parts = token.split("\\.");
        System.out.println("\nDecoded header: " + 
            new String(Base64.getUrlDecoder().decode(parts[0])));
        System.out.println("Decoded payload: " + 
            new String(Base64.getUrlDecoder().decode(parts[1])));
        System.out.println("Signature (hex): " + parts[2].substring(0, 16) + "...");
    }
}