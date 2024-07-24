package com.eric.customerportfolio.dto;

import com.eric.customerportfolio.domain.Ticker;
import com.eric.customerportfolio.domain.TradeAction;

public record StockTradeResponse(Integer customerId,
                                 Ticker ticker,
                                 Integer price,
                                 TradeAction action,
                                 Integer quantity,
                                 Integer totalPrice,
                                 Integer balance) {
}
