package com.dustincode.gateway.security.jwt;

import com.dustincode.gateway.security.jwt.dto.JwtTokenDto;
import com.dustincode.gateway.service.RedisService;
import com.netflix.zuul.context.RequestContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String X_SESSION_ID = "X-Session-Id";
    private static final String X_USER_ID = "X-User-Id";
    private static final String X_USER_EMAIL = "X-User-Email";
    private static final String X_USER_ROLE = "X-User-Role";

    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpServletRequest,
                                    @NonNull HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        resolveTokenFromHeader(httpServletRequest)
            .map(jwtProvider::parseToken)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(this::isTokenNotInBlacklist)
            .ifPresent(tokenDto -> {
                addCustomHeaderToZuulHeader(tokenDto);
                SecurityContextHolder.getContext().setAuthentication(createAuthentication(tokenDto));
            });

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private Optional<String> resolveTokenFromHeader(HttpServletRequest httpServletRequest) {
        String authorization = httpServletRequest.getHeader("Authorization");
        if (StringUtils.isBlank(authorization) || !authorization.contains("Bearer")) {
            return Optional.empty();
        }
        return Optional.of(authorization.substring(7));
    }

    private UsernamePasswordAuthenticationToken createAuthentication(JwtTokenDto jwtTokenDto) {
        String principal = jwtTokenDto.getEmail();
        String credential = jwtTokenDto.getTokenId();
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + jwtTokenDto.getRole())
        );
        return new UsernamePasswordAuthenticationToken(principal, credential, authorities);
    }

    private void addCustomHeaderToZuulHeader(JwtTokenDto jwtTokenDto) {
        RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.addZuulRequestHeader(X_USER_ID, jwtTokenDto.getUserId().toString());
        requestContext.addZuulRequestHeader(X_SESSION_ID, jwtTokenDto.getTokenId());
        requestContext.addZuulRequestHeader(X_USER_EMAIL, jwtTokenDto.getEmail());
        requestContext.addZuulRequestHeader(X_USER_ROLE, jwtTokenDto.getRole());
    }

    public boolean isTokenNotInBlacklist(JwtTokenDto jwtTokenDto) {
        String key = String.format("blacklist:session:%s", jwtTokenDto.getTokenId());
        Boolean exist = redisService.getData(key, Boolean.class);
        return !Boolean.TRUE.equals(exist);
    }
}
