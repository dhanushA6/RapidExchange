package com.trade.TradingPlatform.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JwtProvider {

    // Secret key for signing JWT
    private static final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    /**
     * Generates a JWT token for the given authentication object.
     *
     * @param auth The authentication object containing user details.
     * @return The generated JWT token as a string.
     */
    public static String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        // Build the JWT token
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86400000)) // 24 hours expiration
                .claim("email", auth.getName()) // Add email claim
                .claim("authorities", roles) // Add authorities claim
                .signWith(key) // Sign with the secret key
                .compact();
    }

    /**
     * Extracts the email from the provided JWT token.
     *
     * @param token The JWT token.
     * @return The email claim from the token.
     */
    public static String getEmailFromToken(String token) {
        // Remove the Bearer prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Parse the token and extract claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("email", String.class);
    }

    /**
     * Populates a comma-separated string of authorities from the given collection.
     *
     * @param authorities A collection of granted authorities.
     * @return A comma-separated string of authority names.
     */
    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> authSet = new HashSet<>();
        for (GrantedAuthority ga : authorities) {
            authSet.add(ga.getAuthority());
        }
        return String.join(",", authSet);
    }
}
