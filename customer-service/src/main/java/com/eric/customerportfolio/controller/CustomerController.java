package com.eric.customerportfolio.controller;

import com.eric.customerportfolio.dto.CustomerInformation;
import com.eric.customerportfolio.dto.StockTradeRequest;
import com.eric.customerportfolio.dto.StockTradeResponse;
import com.eric.customerportfolio.entity.Customer;
import com.eric.customerportfolio.service.CustomerService;
import com.eric.customerportfolio.service.TradeService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final TradeService tradeService;

    public CustomerController(CustomerService customerService, TradeService tradeService) {
        this.customerService = customerService;
        this.tradeService = tradeService;
    }

    @GetMapping("/{id}")
    Mono<CustomerInformation> getCustomerInformation(@PathVariable Integer id) {
        return customerService.getCustomerInformation(id);
    }

    @PostMapping("/{id}")
    Mono<StockTradeResponse> postTradeRequest(@PathVariable Integer id, @RequestBody Mono<StockTradeRequest> requestMono) {
        return requestMono.flatMap(req -> tradeService.tradeRequest(id, req));
    }
}
