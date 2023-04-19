package com.service.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author junaid shakeel
 * @date 16/04/2023
 */
@Slf4j
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtUtil {
    String secret;
    String tokenPrefix;

    public boolean validateJwtToken(String jwtToken) {
        try {

            JWT.require(Algorithm.HMAC256(secret.getBytes()))
                    .build().verify(jwtToken);
            return true;
        } catch (SignatureVerificationException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        }  catch (TokenExpiredException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (AlgorithmMismatchException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
    public String getUserNameFromJwtToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret.getBytes()))
                .build()
                .verify(token.replace(tokenPrefix, ""))
                .getSubject();
    }
}
