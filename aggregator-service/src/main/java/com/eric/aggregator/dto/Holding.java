package com.eric.aggregator.dto;

import com.eric.aggregator.domain.Ticker;

public record Holding(Ticker ticker,
                      Integer quantity) {
}
