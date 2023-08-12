package com.example.demo.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Resource not found");
    }

    public ResourceNotFoundException(String property, String value) {
        super("Resource not found - " + property + ": " + value);
    }
}