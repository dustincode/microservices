package com.dustincode.gateway.security.jwt;

import com.dustincode.gateway.config.ApplicationProperties;
import com.dustincode.gateway.security.jwt.dto.JwtTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component
public class JwtProvider {
    private static final String ROLE = "role";
    private static final String USER_ID = "userId";

    private final JwtParser jwtParser;

    public JwtProvider(ApplicationProperties applicationProps) {
        String secret = applicationProps.getJwt().getBase64Secret();
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        jwtParser = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
            .build();
    }

    public Optional<JwtTokenDto> parseToken(String token) {
        return parseClaimsJwt(token)
            .map(claims -> JwtTokenDto
                .builder()
                .tokenId(claims.getId())
                .role(claims.get(ROLE, String.class))
                .userId(claims.get(USER_ID, Long.class))
                .email(claims.getSubject())
                .build()
            );
    }

    private Optional<Claims> parseClaimsJwt(String token) {
        try {
            return Optional.ofNullable(jwtParser.parseClaimsJws(token).getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
