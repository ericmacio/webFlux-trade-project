package com.eric.customerportfolio.exceptions;

import reactor.core.publisher.Mono;

public class ApplicationExceptions {

    public static <T> Mono<T> customerNotFound(Integer id) {
        return Mono.error(new CustomerNotFoundException(id));
    }

    public static <T> Mono<T> insufficientBalance(Integer id) {
        return Mono.error(new InsufficientBalanceException(id));
    }

    public static <T> Mono<T> insufficientShares(Integer id) {
        return Mono.error(new InsufficientSharesException(id));
    }
}
