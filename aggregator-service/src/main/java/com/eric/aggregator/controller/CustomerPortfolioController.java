package com.eric.aggregator.controller;

import com.eric.aggregator.dto.CustomerInformation;
import com.eric.aggregator.dto.StockTradeRequest;
import com.eric.aggregator.dto.StockTradeResponse;
import com.eric.aggregator.dto.TradeRequest;
import com.eric.aggregator.service.CustomerPortfolioService;
import com.eric.aggregator.validator.RequestValidator;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
public class CustomerPortfolioController {

    private final CustomerPortfolioService customerPortfolioService;

    public CustomerPortfolioController(CustomerPortfolioService customerPortfolioService) {
        this.customerPortfolioService = customerPortfolioService;
    }

    @GetMapping("/{customerId}")
    Mono<CustomerInformation> getCustomerInformation(@PathVariable Integer customerId) {
        return customerPortfolioService.getCustomerInformation(customerId);
    }

    @PostMapping("/{customerId}/trade")
    Mono<StockTradeResponse> postTradeRequest(@PathVariable Integer customerId, @RequestBody Mono<TradeRequest> requestMono) {
        return requestMono
                .transform(RequestValidator.validate())
                .flatMap(request -> customerPortfolioService.sendTradeRequest(customerId, request));
    }
}
