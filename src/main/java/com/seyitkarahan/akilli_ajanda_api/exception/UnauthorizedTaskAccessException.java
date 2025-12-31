package com.seyitkarahan.akilli_ajanda_api.exception;

public class UnauthorizedTaskAccessException extends RuntimeException {

    public UnauthorizedTaskAccessException(String message) {
        super(message);
    }
}
