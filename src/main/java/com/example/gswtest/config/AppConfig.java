package com.example.gswtest.config;

import com.example.gswtest.controller.BankAccountController;
import com.example.gswtest.controller.TransactionController;
import com.example.gswtest.dao.BankAccountDao;
import com.example.gswtest.dao.TransactionDao;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {

    private final TransactionDao transactionDao;
    private final BankAccountController bankAccountController;


    @Autowired
    public AppConfig(TransactionDao transactionDao, BankAccountController bankAccountController){
        this.transactionDao = transactionDao;
        this.bankAccountController = bankAccountController;
    }


    @Bean
    public TransactionController transactionController() {
        return new TransactionController(transactionDao, bankAccountController);
    }


}
