package com.example.exception.token;

public class TokenExpiredException extends TokenException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
