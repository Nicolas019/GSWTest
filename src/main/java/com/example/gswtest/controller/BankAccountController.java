package com.example.gswtest.controller;

import com.example.gswtest.dao.BankAccountDao;
import com.example.gswtest.model.BankAccount;

import java.util.Optional;

public class BankAccountController {
    BankAccountDao bankAccountDao;

    public BankAccountController(BankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

    public BankAccount findAccount(String iban) {
        Optional<BankAccount> optionalBankAccount = bankAccountDao.findById(iban);
        return optionalBankAccount.orElse(null);
    }
}
