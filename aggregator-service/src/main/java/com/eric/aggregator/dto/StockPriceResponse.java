package com.eric.aggregator.dto;

import com.eric.aggregator.domain.Ticker;

public record StockPriceResponse(Ticker ticker,
                                 Integer price) {
}
