package com.personal_finance.exception;

public class ExpenseAlreadyPaidException extends RuntimeException {
    public ExpenseAlreadyPaidException(String message) {
        super(message);
    }
}
