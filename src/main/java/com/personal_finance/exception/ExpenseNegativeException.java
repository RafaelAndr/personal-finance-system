package com.personal_finance.exception;

public class ExpenseNegativeException extends RuntimeException {
    public ExpenseNegativeException(String message) {
        super(message);
    }
}
