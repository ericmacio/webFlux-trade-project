package com.eric.aggregator.dto;

import com.eric.aggregator.domain.Ticker;
import com.eric.aggregator.domain.TradeAction;

public record TradeRequest(
        Ticker ticker,
        TradeAction action,
        Integer quantity) {
}
