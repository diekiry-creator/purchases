package com.davydov.purchases.dto;

import javax.validation.constraints.NotNull;

public class CreatePurchase {
    @NotNull
    UserRequisites requisites;
    @NotNull
    Long bookTypeId;
    @NotNull
    Long amountBooks;

    public UserRequisites getRequisites() {
        return requisites;
    }

    public void setRequisites(UserRequisites requisites) {
        this.requisites = requisites;
    }

    public Long getBookTypeId() {
        return bookTypeId;
    }

    public void setBookTypeId(Long bookTypeId) {
        this.bookTypeId = bookTypeId;
    }

    public Long getAmountBooks() {
        return amountBooks;
    }

    public void setAmountBooks(Long amountBooks) {
        this.amountBooks = amountBooks;
    }
}
