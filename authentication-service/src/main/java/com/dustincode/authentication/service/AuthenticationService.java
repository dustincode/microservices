package com.dustincode.authentication.service;

import com.dustincode.authentication.service.dto.LoginDto;
import com.dustincode.authentication.service.dto.LoginResultDto;
import com.dustincode.authentication.service.dto.UserDto;

public interface AuthenticationService {
    UserDto createUser(UserDto request);
    LoginResultDto login(LoginDto request);
    void logout(String sessionId);
}
