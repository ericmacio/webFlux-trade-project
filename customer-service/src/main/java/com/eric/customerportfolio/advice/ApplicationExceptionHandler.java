package com.eric.customerportfolio.advice;

import com.eric.customerportfolio.exceptions.CustomerNotFoundException;
import com.eric.customerportfolio.exceptions.InsufficientBalanceException;
import com.eric.customerportfolio.exceptions.InsufficientSharesException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.util.function.Consumer;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ProblemDetail handleException(CustomerNotFoundException ex) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, ex, (problemDetail) -> {
            problemDetail.setType(URI.create("http://example.com/problems/customer-not-found"));
            problemDetail.setTitle("Customer not found");
        });
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ProblemDetail handleException(InsufficientBalanceException ex) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, ex, (problemDetail) -> {
            problemDetail.setType(URI.create("http://example.com/problems/insufficient-balance"));
            problemDetail.setTitle("Insufficient balance");
        });
    }

    @ExceptionHandler(InsufficientSharesException.class)
    public ProblemDetail handleException(InsufficientSharesException ex) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, ex, (problemDetail) -> {
            problemDetail.setType(URI.create("http://example.com/problems/insufficient-shares"));
            problemDetail.setTitle("Insufficient shares");
        });
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, Exception ex, Consumer<ProblemDetail> consumer) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        consumer.accept(problemDetail);
        return problemDetail;
    }
}
