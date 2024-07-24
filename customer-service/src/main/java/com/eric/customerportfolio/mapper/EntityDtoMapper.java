package com.eric.customerportfolio.mapper;

import com.eric.customerportfolio.domain.Ticker;
import com.eric.customerportfolio.dto.CustomerInformation;
import com.eric.customerportfolio.dto.Holding;
import com.eric.customerportfolio.dto.StockTradeRequest;
import com.eric.customerportfolio.dto.StockTradeResponse;
import com.eric.customerportfolio.entity.Customer;
import com.eric.customerportfolio.entity.PortfolioItem;

import java.util.List;

public class EntityDtoMapper {

    public static CustomerInformation toCustomerInformation(Customer customer, List<PortfolioItem> items) {
        List<Holding> holdings = items.stream()
                .map(i -> new Holding(i.getTicker(), i.getQuantity()))
                .toList();
        return new CustomerInformation(customer.getId(), customer.getName(), customer.getBalance(), holdings);
    }

    public static PortfolioItem toPortFolioItem(Integer customerId, Ticker ticker) {
        PortfolioItem portfolioItem = new PortfolioItem();
        portfolioItem.setCustomerId(customerId);
        portfolioItem.setTicker(ticker);
        portfolioItem.setQuantity(0);
        return portfolioItem;
    }

    public static StockTradeResponse toStockTradeResponse(Integer customerId, StockTradeRequest request, Integer balance) {
        return new StockTradeResponse(
                customerId,
                request.ticker(),
                request.price(),
                request.action(),
                request.quantity(),
                request.totalPrice(),
                balance);

    }
}
