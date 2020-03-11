package com.davydov.purchases.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "purchase")
public class Purchase implements Serializable {

    private static final long serialVersionUID = -2343243243242432341L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "userId")
    private long userId;

    @Column(name = "bookId")
    private long bookId;

    @Column(name = "price")
    private double price;

    protected Purchase() {
    }

    public Purchase(long userId, long bookId, double price) {
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
        return String.format("Purchase[id='%d', userId='%d', bookId='%d', price=''%f]", id, userId, bookId, price);
    }
}
