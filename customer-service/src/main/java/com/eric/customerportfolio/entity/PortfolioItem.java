package com.eric.customerportfolio.entity;

import com.eric.customerportfolio.domain.Ticker;
import org.springframework.data.annotation.Id;

public class PortfolioItem {

    @Id
    private Integer id;
    private Integer customerId;
    private Ticker ticker;
    private Integer quantity;

    public PortfolioItem() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "PortfolioItem{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", ticker=" + ticker +
                ", quantity=" + quantity +
                '}';
    }
}
