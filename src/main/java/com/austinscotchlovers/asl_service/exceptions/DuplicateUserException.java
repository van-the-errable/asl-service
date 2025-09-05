package com.austinscotchlovers.asl_service.exceptions;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) {
        super(message);
    }
}
