package com.davydov.purchases.dto;

import javax.validation.constraints.NotNull;

public class BookInfo {
    @NotNull
    Double rentPrice;
    @NotNull
    Double purchasePrice;

    public Double getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(Double rentPrice) {
        this.rentPrice = rentPrice;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}
