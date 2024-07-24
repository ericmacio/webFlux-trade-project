package com.eric.aggregator.advice;

import com.eric.aggregator.exceptions.CustomerNotFoundException;
import com.eric.aggregator.exceptions.InvalidTradeRequestException;
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

    @ExceptionHandler(InvalidTradeRequestException.class)
    public ProblemDetail handleException(InvalidTradeRequestException ex) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, ex, (problemDetail) -> {
            problemDetail.setType(URI.create("http://example.com/problems/invalid-trade-request"));
            problemDetail.setTitle("Invalid trade request");
        });
    }


    private ProblemDetail buildProblemDetail(HttpStatus status, Exception ex, Consumer<ProblemDetail> consumer) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        consumer.accept(problemDetail);
        return problemDetail;
    }
}
