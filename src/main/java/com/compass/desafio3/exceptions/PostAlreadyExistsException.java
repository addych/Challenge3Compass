package com.compass.desafio3.exceptions;

public class PostAlreadyExistsException extends RuntimeException {
    public PostAlreadyExistsException(String message) {
        super(message);
    }
}
