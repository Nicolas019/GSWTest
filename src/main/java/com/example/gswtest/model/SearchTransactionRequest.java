package com.example.gswtest.model;

import lombok.Data;

/**
 * Request to search Transactions
 * IBAN --> Account iban
 * sort --> Transaction sorting method by amount {asc, desc}
 */
@Data
public class SearchTransactionRequest {
    private String iban;
    private String sort;
}
