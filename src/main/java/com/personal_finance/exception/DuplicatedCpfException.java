package com.personal_finance.exception;

public class DuplicatedCpfException extends RuntimeException {
    public DuplicatedCpfException(String message) {
        super(message);
    }
}
