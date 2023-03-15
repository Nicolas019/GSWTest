package com.example.gswtest.config;

import com.example.gswtest.controller.BankAccountController;
import com.example.gswtest.controller.TransactionController;
import com.example.gswtest.dao.BankAccountDao;
import com.example.gswtest.dao.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final TransactionDao transactionDao;
    private final BankAccountDao bankAccountDao;


    @Autowired
    public AppConfig(TransactionDao transactionDao, BankAccountDao bankAccountDao){
        this.transactionDao = transactionDao;
        this.bankAccountDao = bankAccountDao;
    }


    @Bean
    public TransactionController transactionController() {
        return new TransactionController(transactionDao, bankAccountController());
    }

    @Bean
    public BankAccountController bankAccountController() {
        return new BankAccountController(bankAccountDao);
    }



}
