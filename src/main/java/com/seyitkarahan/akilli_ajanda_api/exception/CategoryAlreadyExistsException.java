package com.seyitkarahan.akilli_ajanda_api.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
