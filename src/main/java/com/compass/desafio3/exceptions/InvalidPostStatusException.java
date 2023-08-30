package com.compass.desafio3.exceptions;

public class InvalidPostStatusException extends RuntimeException {
    public InvalidPostStatusException(String message) {
        super(message);
    }
}
