package com.example.exception.token;

public class InvalidTokenSignatureException extends TokenException {

    public InvalidTokenSignatureException(String message) {
        super(message);
    }
}
