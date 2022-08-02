package com.dustincode.authentication.service.impl;

import com.dustincode.authentication.config.ApplicationProperties;
import com.dustincode.authentication.entity.Role;
import com.dustincode.authentication.entity.User;
import com.dustincode.authentication.entity.UserSession;
import com.dustincode.authentication.exception.BadRequestException;
import com.dustincode.authentication.exception.UnauthorizedException;
import com.dustincode.authentication.repository.RoleRepository;
import com.dustincode.authentication.repository.UserRepository;
import com.dustincode.authentication.repository.UserSessionRepository;
import com.dustincode.authentication.security.JwtProvider;
import com.dustincode.authentication.security.dto.JwtTokenDto;
import com.dustincode.authentication.service.AuthenticationService;
import com.dustincode.authentication.service.RedisService;
import com.dustincode.authentication.service.dto.LoginDto;
import com.dustincode.authentication.service.dto.LoginResultDto;
import com.dustincode.authentication.service.dto.UserDto;
import com.dustincode.authentication.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.dustincode.authentication.constant.AppConstants.BLANK_STR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    /*** Repositories */
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserSessionRepository sessionRepository;

    /** Services */
    private final RedisService redisService;

    /** Others */
    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationProperties properties;

    @Override
    @Transactional
    public UserDto createUser(UserDto request) {
        boolean isExistingEmail = userRepository
            .findByEmail(request.getEmail())
            .isPresent();

        if (isExistingEmail) {
            throw new BadRequestException("Email already exist.");
        }

        Role role = roleRepository
            .findByName(request.getRole())
            .orElseThrow(() -> new BadRequestException("Role is not exist"));

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(role)
            .build();

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public LoginResultDto login(LoginDto request) {
        User user = userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(UnauthorizedException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException();
        }

        JwtTokenDto tokenDto = jwtProvider.createToken(user);

        saveUserSession(user, tokenDto);

        return LoginResultDto
            .builder()
            .token(tokenDto.getToken())
            .build();
    }

    @Override
    @Transactional
    public void logout(String sessionId) {
        Optional<UserSession> userSession = sessionRepository
            .findBySessionId(sessionId);

        if (userSession.isEmpty()) return;

        sessionRepository.delete(userSession.get());
        pushSessionIdToBlacklist(sessionId);
    }

    private void saveUserSession(User user, JwtTokenDto tokenDto) {
        UserSession session = new UserSession(user, tokenDto);
        sessionRepository.save(session);
    }

    private void pushSessionIdToBlacklist(String sessionId) {
        redisService.setData(
            String.format("blacklist:session:%s", sessionId),
            Boolean.TRUE,
            properties.getJwt().getTokenValidityInSeconds()
        );
    }
}
