//package com.example.BuyerApp.jwt;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import lombok.Getter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import java.security.Key;
//import java.util.Date;
//import java.util.function.Function;
//
//@Component
//public class JwtUtil {
//
//    @Getter
//    @Value("${app.jwtAuthTokenSecret}")
//    private String jwtAuthTokenSecret;
//
//
//    private static final long JWT_TOKEN_VALIDITY = 1 * 60 * 60;
//
//
//
//    private Key getSignKey(String secretKey) {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    public Boolean validateToken(String token, String phoneNumber) {
//        final String subject = extractSubject(token);
//        return (subject.equals(phoneNumber) && !isTokenExpired(token));
//    }
//
//    public String extractSubject(String token) {
//
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    private Boolean isTokenExpired(String token) {
//
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignKey(jwtAuthTokenSecret))
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//}
//
