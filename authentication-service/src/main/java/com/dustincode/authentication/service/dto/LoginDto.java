package com.dustincode.authentication.service.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
}
