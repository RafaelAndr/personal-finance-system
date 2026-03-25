package com.personal_finance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
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

    @ExceptionHandler(DuplicatedCpfException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleDuplicatedCpfException(DuplicatedCpfException e){

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());

        problemDetail.setTitle("Conflict: Existing Cpf");

        return problemDetail;
    }
}
