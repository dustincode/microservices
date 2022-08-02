package com.dustincode.gateway.config;

import com.dustincode.gateway.security.jwt.JwtFilter;
import com.dustincode.gateway.security.jwt.JwtProvider;
import com.dustincode.gateway.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .exceptionHandling()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(
                new JwtFilter(jwtProvider, redisService),
                UsernamePasswordAuthenticationFilter.class
            )
            .authorizeRequests()
            .antMatchers("/**/api/logout").authenticated()
            .antMatchers("/**/api/reviewers/**").hasAuthority("ROLE_STAFF")
            .antMatchers("/log/api/**").hasAuthority("ROLE_ADMIN")
            .antMatchers("/**").permitAll();
    }
}
