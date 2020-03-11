package com.davydov.purchases.model;

public class PurchaseUI  {

    private long userId;
    private long bookId;
    private double price;

    protected PurchaseUI() {
    }

    public PurchaseUI(long userId, long bookId, double price) {
        this.userId = userId;
        this.bookId = bookId;
        this.price = price;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String toString() {
        return String.format("Purchase[userId='%d', bookId='%d', price=''%f]", userId, bookId, price);
    }
}
