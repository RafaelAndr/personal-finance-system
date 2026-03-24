package com.nutrition_system.exception;

public class DuplicatedCpfException extends RuntimeException {
    public DuplicatedCpfException(String message) {
        super(message);
    }
}
