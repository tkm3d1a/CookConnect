package com.tkforgeworks.cookconnect.userservice.errorhandler;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
