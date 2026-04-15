package com.personal_finance.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e){

        Map<String, String> errors = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));

        var problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation Failed"
        );

        problemDetail.setTitle("Invalid request data");
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInvalidJson(HttpMessageNotReadableException e){

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Invalid Json");
        problemDetail.setDetail("Request body is malformed");
        problemDetail.setType(URI.create("https://api.coderbank.com.br"));

        return problemDetail;
    }

    @ExceptionHandler(AccountHasNoUserException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleAccountHasNoUserException(AccountHasNoUserException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        problemDetail.setTitle("Business rule violation");
        problemDetail.setType(URI.create("https://api.spring-finance.com.br/erros/account-no-user"));
        problemDetail.setDetail(e.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(ExpenseAlreadyPaidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail expenseAlreadyPaidException(ExpenseAlreadyPaidException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        problemDetail.setTitle("Business rule violation");
        problemDetail.setType(URI.create("https://api.spring-finance.com.br/erros/account-no-user"));
        problemDetail.setDetail(e.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(NegativeValueException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleNegativeValueException(NegativeValueException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        problemDetail.setTitle("Business rule violation");
        problemDetail.setType(URI.create("https://api.spring-finance.com.br/erros/account-no-user"));
        problemDetail.setDetail(e.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(AccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAccessForbiddenException(AccessForbiddenException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

        problemDetail.setTitle("Business rule violation");
        problemDetail.setType(URI.create("https://api.spring-finance.com.br/erros/account-no-user"));
        problemDetail.setDetail(e.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleInsufficientBalanceException(InsufficientBalanceException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);

        problemDetail.setTitle("Business rule violation");
        problemDetail.setType(URI.create("https://api.spring-finance.com.br/erros/account-no-user"));
        problemDetail.setDetail(e.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleEntityAlreadyExistsException(EntityAlreadyExistsException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problemDetail.setTitle("Business rule violation");
        problemDetail.setType(URI.create("https://api.spring-finance.com.br/erros/entity_alrealdy_exists"));
        problemDetail.setDetail(e.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problemDetail.setTitle("Business rule violation");
        problemDetail.setType(URI.create("https://api.spring-finance.com.br/erros/account-no-user"));
        problemDetail.setDetail(e.getMessage());

        return problemDetail;
    }
}
