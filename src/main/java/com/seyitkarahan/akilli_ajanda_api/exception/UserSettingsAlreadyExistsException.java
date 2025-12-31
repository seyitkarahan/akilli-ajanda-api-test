package com.seyitkarahan.akilli_ajanda_api.exception;

public class UserSettingsAlreadyExistsException extends RuntimeException {

    public UserSettingsAlreadyExistsException(String message) {
        super(message);
    }
}
