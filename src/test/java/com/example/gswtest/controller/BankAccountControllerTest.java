package com.example.gswtest.controller;

import com.example.gswtest.dao.BankAccountDao;
import com.example.gswtest.model.BankAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class BankAccountControllerTest {
    private static BankAccountController testee;
    @Mock
    BankAccountDao bankAccountDaoMock;
    
    @BeforeEach
    void initialize(){
        MockitoAnnotations.openMocks(this);
        testee = new BankAccountController(bankAccountDaoMock);
    }
    
    @Test
    void findAccount_withValidAccount_returnAccount(){
        //Setup
        String iban = "test";
        BankAccount bankAccountMock = Mockito.mock(BankAccount.class);
        Optional<BankAccount> bankAccountOptionalMock = Optional.of(bankAccountMock);
        when(bankAccountDaoMock.findById(iban)).thenReturn(bankAccountOptionalMock);
        //Test
        BankAccount accountTested = testee.findAccount(iban);
        //Verify
        Assertions.assertEquals(accountTested, bankAccountMock);
    }

    @Test
    void findAccount_withInvalidAccount_returnNull(){
        //Setup
        String iban = "test";
        Optional<BankAccount> bankAccountOptionalMock = Optional.empty();
        when(bankAccountDaoMock.findById(iban)).thenReturn(bankAccountOptionalMock);
        //Test
        BankAccount accountTested = testee.findAccount(iban);
        //Verify
        Assertions.assertNull(accountTested);
    }
}
