package com.davydov.purchases.dto;

import javax.validation.constraints.NotNull;

public class UserOperation {
    @NotNull
    UserRequisites requisites;

    Double price;

    public UserOperation(@NotNull UserRequisites requisites, @NotNull Double price) {
        this.requisites = requisites;
        this.price = price;
    }

    public UserRequisites getRequisites() {
        return requisites;
    }

    public void setRequisites(UserRequisites requisites) {
        this.requisites = requisites;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
