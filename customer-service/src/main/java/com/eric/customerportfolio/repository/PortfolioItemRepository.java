package com.eric.customerportfolio.repository;

import com.eric.customerportfolio.domain.Ticker;
import com.eric.customerportfolio.entity.PortfolioItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PortfolioItemRepository extends ReactiveCrudRepository<PortfolioItem, Integer> {

    Flux<PortfolioItem> findAllByCustomerId(Integer customerId);
    Mono<PortfolioItem> findByCustomerIdAndTicker(Integer customerId, Ticker ticker);

}
