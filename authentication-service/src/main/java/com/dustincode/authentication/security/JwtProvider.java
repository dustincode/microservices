package com.dustincode.authentication.security;

import com.dustincode.authentication.config.ApplicationProperties;
import com.dustincode.authentication.entity.User;
import com.dustincode.authentication.security.dto.JwtTokenDto;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    private static final String ROLE = "role";
    private static final String USER_ID = "userId";

    private JwtParser jwtParser;
    private ApplicationProperties applicationProps;

    private final Key key;
    private final long tokenValidityInMiliseconds;

    public JwtProvider(ApplicationProperties applicationProps) {
        this.applicationProps = applicationProps;
        this.tokenValidityInMiliseconds = applicationProps.getJwt()
            .getTokenValidityInSeconds() * 1000;
        String secret = applicationProps.getJwt().getBase64Secret();
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }


    public JwtTokenDto createToken(User user) {
        String tokenId = UUID.randomUUID().toString();
        String role = user.getRole().getName();
        Date iat = new Date(System.currentTimeMillis());
        Date exp = new Date(System.currentTimeMillis() + tokenValidityInMiliseconds);
        String token = Jwts.builder()
            .setSubject(user.getEmail())
            .setIssuedAt(iat)
            .setExpiration(exp)
            .claim(ROLE, role)
            .claim(USER_ID, user.getId())
            .setId(tokenId)
            .signWith(key)
            .compact();

        return JwtTokenDto
            .builder()
            .tokenId(tokenId)
            .token(token)
            .email(user.getEmail())
            .role(role)
            .userId(user.getId())
            .expiredTime(exp)
            .build();
    }
}
