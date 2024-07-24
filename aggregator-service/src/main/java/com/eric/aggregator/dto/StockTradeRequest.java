package com.eric.aggregator.dto;

import com.eric.aggregator.domain.Ticker;
import com.eric.aggregator.domain.TradeAction;

public record StockTradeRequest(Ticker ticker,
                                Integer price,
                                Integer quantity,
                                TradeAction action) {

}
