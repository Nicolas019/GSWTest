package com.example.gswtest.constant;

public enum TransactionStatusCriteria {
    PENDING("PENDING"),
    SETTLED("SETTLED"),
    FUTURE("FUTURE"),
    INVALID("INVALID");

    private final String status;

    TransactionStatusCriteria(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
