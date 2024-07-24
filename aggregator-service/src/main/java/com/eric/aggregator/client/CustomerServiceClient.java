package com.eric.aggregator.client;

import com.eric.aggregator.dto.CustomerInformation;
import com.eric.aggregator.dto.StockTradeRequest;
import com.eric.aggregator.dto.StockTradeResponse;
import com.eric.aggregator.exceptions.ApplicationExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException.BadRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class CustomerServiceClient {

    private static final Logger log = LoggerFactory.getLogger(StockServiceClient.class);
    private WebClient client;

    public CustomerServiceClient(WebClient client) {
        this.client = client;
    }

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId) {
        log.info("customerId: {}", customerId);
        return client.get()
                .uri("/customers/{customerId}", customerId)
                .retrieve()
                .bodyToMono(CustomerInformation.class)
                .onErrorResume(NotFound.class, ex -> ApplicationExceptions.customerNotFound(customerId));
    }

    public Mono<StockTradeResponse> sendTradeRequest(Integer customerId, StockTradeRequest request) {
        return client.post()
                .uri("/customers/{id}/trade", customerId)
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToMono(StockTradeResponse.class)
                .onErrorResume(NotFound.class, ex -> ApplicationExceptions.customerNotFound(customerId))
                .onErrorResume(BadRequest.class, this::handleException);
    }

    private <T> Mono handleException(BadRequest exception) {
        ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
        String message = Objects.nonNull(problemDetail) ? problemDetail.getDetail() : exception.getMessage();
        log.error("customer service problem detail: {}", problemDetail);
        return ApplicationExceptions.invalidTradeRequest(message);
    }
}
