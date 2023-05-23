package com.example.gswtest.config;

import com.example.gswtest.controller.BankAccountController;
import com.example.gswtest.controller.TransactionController;
import com.example.gswtest.dao.BankAccountDao;
import com.example.gswtest.dao.TransactionDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AppConfigTest {
    private static AppConfig testee;
    @Mock
    private static TransactionDao transactionDao;
    @Mock
    private static BankAccountController bankAccountController;

    @BeforeEach
    void initialize() {
        MockitoAnnotations.openMocks(this);
        testee = new AppConfig(transactionDao, bankAccountController);
    }

    @Test
    void getTransactionController_returnValidInstance(){
        TransactionController controller = testee.transactionController();
        Assertions.assertNotNull(controller);
    }

    @Test
    void getBankAccountController_returnValidInstance(){
        BankAccountController controller = testee.getBankAccountController();
        Assertions.assertNotNull(controller);
    }
}
