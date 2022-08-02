package com.dustincode.authentication.controller;

import com.dustincode.authentication.service.AuthenticationService;
import com.dustincode.authentication.service.dto.LoginDto;
import com.dustincode.authentication.service.dto.LoginResultDto;
import com.dustincode.authentication.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.dustincode.authentication.constant.HeaderConstants.X_SESSION_ID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResultDto> login(@Valid @RequestBody LoginDto request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto request) {
        return ResponseEntity.ok(authenticationService.createUser(request));
    }

    @DeleteMapping(value = "/logout")
    public ResponseEntity<Void> logout(@RequestHeader(X_SESSION_ID) String sessionId) {
        authenticationService.logout(sessionId);
        return ResponseEntity.noContent().build();
    }
}
