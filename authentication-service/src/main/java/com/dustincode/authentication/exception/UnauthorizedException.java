package com.dustincode.authentication.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Email or password invalid");
    }
}
