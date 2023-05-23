package com.example.gswtest.controller;

import com.example.gswtest.dao.BankAccountDao;
import com.example.gswtest.model.BankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/test")
public class BankAccountController {
    private final BankAccountDao bankAccountDao;

    @Autowired
    public BankAccountController(BankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

    public BankAccount findAccount(String iban) {
        Optional<BankAccount> optionalBankAccount = bankAccountDao.findById(iban);
        return optionalBankAccount.orElse(null);
    }

    /**
     * For visibility, it will show all the accounts registered in the system (HSQL in memory DB)
     * Didn't include any Features as it wasn't on the requirements and would increase the length of the test
     */
    @GetMapping("/account")
    public List<BankAccount> findAccount() {
        return bankAccountDao.findAll();
    }
}
