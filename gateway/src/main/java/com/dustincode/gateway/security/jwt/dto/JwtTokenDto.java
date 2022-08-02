package com.dustincode.gateway.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenDto {
    private Long userId;
    private String tokenId;
    private String token;
    private String email;
    private String role;
    private Date expiredTime;
}
