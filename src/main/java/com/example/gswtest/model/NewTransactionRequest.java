package com.example.gswtest.model;

import lombok.Data;

import java.util.Date;

@Data
public class NewTransactionRequest {
    private String reference;
    private String IBAN;
    private Date date;
    private Double amount;
    private Double fee;
    private String description;
}
