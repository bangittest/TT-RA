package com.example.warehousemanagement_team1.security.jwt;

import com.example.warehousemanagement_team1.security.user_principle.UserPrinciple;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTProvider {
    @Value("${expired}")
    private Long EXPIRED;
    @Value("${secret_key}")
    private String SECRET_KEY;
    private final Logger LOGGER = LoggerFactory.getLogger(JWTEntryPoint.class);

    public String generateToken(UserPrinciple userPrinciple) {
        return Jwts.builder()
                .setSubject(userPrinciple.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + EXPIRED))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expiredJwtException) {
            LOGGER.error("Expired Token {}", expiredJwtException.getMessage());
        } catch (MalformedJwtException malformedJwtException) {
            LOGGER.error("Invalid format {}", malformedJwtException.getMessage());
        } catch (UnsupportedJwtException unsupportedJwtException) {
            LOGGER.error("Unsupported token format {}", unsupportedJwtException.getMessage());
        } catch (SignatureException signatureException) {
            LOGGER.error("Invalid Signature Token {}", signatureException.getMessage());
        }
        return false;

    }

    public String getUsernameToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

}
