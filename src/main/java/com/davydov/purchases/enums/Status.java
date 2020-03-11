package com.davydov.purchases.enums;

public enum Status {
    SUCCESS(0),
    FAIL(-1);

    private int value;

    Status(int value) {
        this.value = value;
    }
}
