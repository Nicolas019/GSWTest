package com.example.gswtest.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


@Entity
@Data
public class Transaction {
    @Id
    private String reference;
    private String IBAN;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    private Double amount;
    private Double fee;
    private String description;

}
