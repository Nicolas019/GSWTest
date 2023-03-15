package com.example.gswtest.model;

import lombok.Data;

@Data
public class TransactionStatusRequest {
    private String reference;
    private String channel;
}
