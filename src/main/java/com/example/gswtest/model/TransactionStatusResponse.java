package com.example.gswtest.model;

import lombok.Data;

@Data
public class TransactionStatusResponse {
    private String reference;
    private String status;
    private Double amount;
    private Double fee;
}
