package com.davydov.purchases.dto;

import javax.validation.constraints.NotNull;

public class bookOperation {
    @NotNull
    Long bookTypeId;
    @NotNull
    Long amountBooks;

    public bookOperation(@NotNull Long bookTypeId, @NotNull Long amountBooks) {
        this.bookTypeId = bookTypeId;
        this.amountBooks = amountBooks;
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
