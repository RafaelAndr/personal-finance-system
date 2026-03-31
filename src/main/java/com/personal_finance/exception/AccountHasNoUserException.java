package com.personal_finance.exception;

public class AccountHasNoUserException extends RuntimeException {
    public AccountHasNoUserException(String message) {
        super(message);
    }
}
