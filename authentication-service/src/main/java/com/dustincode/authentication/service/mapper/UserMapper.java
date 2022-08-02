package com.dustincode.authentication.service.mapper;

import com.dustincode.authentication.entity.User;
import com.dustincode.authentication.service.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto
            .builder()
            .role(user.getRole().getName())
            .userId(user.getId())
            .email(user.getEmail())
            .build();
    }
}
