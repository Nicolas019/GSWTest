package com.example.gswtest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class BankAccount {

    @Id
    private String iban;
    private Double balance;
}
