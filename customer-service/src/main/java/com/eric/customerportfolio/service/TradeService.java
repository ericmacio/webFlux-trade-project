package com.eric.customerportfolio.service;

import com.eric.customerportfolio.domain.Ticker;
import com.eric.customerportfolio.domain.TradeAction;
import com.eric.customerportfolio.dto.CustomerInformation;
import com.eric.customerportfolio.dto.StockTradeRequest;
import com.eric.customerportfolio.dto.StockTradeResponse;
import com.eric.customerportfolio.entity.Customer;
import com.eric.customerportfolio.entity.PortfolioItem;
import com.eric.customerportfolio.exceptions.ApplicationExceptions;
import com.eric.customerportfolio.mapper.EntityDtoMapper;
import com.eric.customerportfolio.repository.CustomerRepository;
import com.eric.customerportfolio.repository.PortfolioItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;


@Service
public class TradeService {

    private static final Logger log = LoggerFactory.getLogger(TradeService.class);

    private final CustomerRepository customerRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public TradeService(CustomerRepository customerRepository, PortfolioItemRepository portfolioItemRepository) {
        this.customerRepository = customerRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    @Transactional
    public Mono<StockTradeResponse> tradeRequest(Integer customerId, StockTradeRequest request) {
        return switch (request.action()) {
            case BUY -> this.buyStock(customerId, request);
            case SELL -> this.sellStock(customerId, request);
        };
    }

    private Mono<StockTradeResponse> buyStock(Integer customerId, StockTradeRequest request) {

        Mono<Customer> validatedCustomerMono =
                customerRepository.findById(customerId)
                    .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                    .filter(customer -> customer.getBalance() >= request.totalPrice())
                    .switchIfEmpty(ApplicationExceptions.insufficientBalance(customerId));

        Mono<PortfolioItem> portfolioItemMono =
                portfolioItemRepository.findByCustomerIdAndTicker(customerId, request.ticker())
                    .defaultIfEmpty(EntityDtoMapper.toPortFolioItem(customerId, request.ticker()));

        return validatedCustomerMono.zipWhen(customer -> portfolioItemMono) // give customer when found then get item from next publisher
                .flatMap(t -> executeBuy(t.getT1(), t.getT2(), request));
    }

    private Mono<StockTradeResponse> sellStock(Integer customerId, StockTradeRequest request) {

        Mono<Customer> validatedCustomerMono =
                customerRepository.findById(customerId)
                        .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId));

        Mono<PortfolioItem> portfolioItemMono =
                portfolioItemRepository.findByCustomerIdAndTicker(customerId, request.ticker())
                        .filter(portfolioItem -> portfolioItem.getQuantity() >= request.quantity())
                        .switchIfEmpty(ApplicationExceptions.insufficientShares(customerId));

        return validatedCustomerMono.zipWhen(customer -> portfolioItemMono) // give customer when found then get item from next publisher
                .flatMap(t -> executeSell(t.getT1(), t.getT2(), request));

    }

    private Mono<StockTradeResponse> executeBuy(Customer customer, PortfolioItem portfolioItem, StockTradeRequest request) {
        Integer newBalance = customer.getBalance() - request.totalPrice();
        customer.setBalance(newBalance);
        portfolioItem.setQuantity(portfolioItem.getQuantity() + request.quantity());
        return saveAndGetResponse(customer, portfolioItem, request);
    }

    private Mono<StockTradeResponse> executeSell(Customer customer, PortfolioItem portfolioItem, StockTradeRequest request) {
        Integer newBalance = customer.getBalance() + request.totalPrice();
        customer.setBalance(newBalance);
        portfolioItem.setQuantity(portfolioItem.getQuantity() - request.quantity());
        return saveAndGetResponse(customer, portfolioItem, request);
    }

    private Mono<StockTradeResponse> saveAndGetResponse(Customer customer, PortfolioItem portfolioItem, StockTradeRequest request) {
        StockTradeResponse response = EntityDtoMapper.toStockTradeResponse(customer.getId(), request, customer.getBalance());
        return Mono.zip(customerRepository.save(customer), portfolioItemRepository.save(portfolioItem))
                .thenReturn(response);
    }

}
