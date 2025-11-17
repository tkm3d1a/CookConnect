package com.tkforgeworks.cookconnect.socialservice.errorhandler;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
