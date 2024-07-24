package com.eric.customerportfolio.dto;

import com.eric.customerportfolio.domain.Ticker;

public record Holding(Ticker ticker,
                      Integer quantity) {
}
