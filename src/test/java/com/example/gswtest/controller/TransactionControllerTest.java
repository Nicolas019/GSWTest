package com.example.gswtest.controller;

import com.example.gswtest.constant.TransactionChannelCriteria;
import com.example.gswtest.constant.TransactionStatusCriteria;
import com.example.gswtest.dao.TransactionDao;
import com.example.gswtest.exception.InvalidInputException;
import com.example.gswtest.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class TransactionControllerTest {

    private static TransactionController testee;
    private static NewTransactionRequest newTransactionRequest;
    private static SearchTransactionRequest searchTransactionRequest;
    private static TransactionStatusRequest transactionStatusRequest;
    private static BankAccount bankAccount;
    private static Transaction transaction;
    private static final String IBAN = "testIban";
    private static final String REFERENCE = "test";
    private static final Double ACCOUNT_BALANCE = 20.0;
    private static final Double TRANSACTION_AMOUNT = 12.5;
    private static final Double TRANSACTION_FEE = 1.0;
    Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    @Mock
    TransactionDao transactionDaoMock;
    @Mock
    BankAccountController bankAccountControllerMock;

    @BeforeEach
    void initialize(){
        MockitoAnnotations.openMocks(this);
        testee = new TransactionController(transactionDaoMock, bankAccountControllerMock);

        //Setup New Transaction
        newTransactionRequest = new NewTransactionRequest();
        newTransactionRequest.setReference(REFERENCE);
        newTransactionRequest.setIBAN(IBAN);
        newTransactionRequest.setDate(new Date());
        newTransactionRequest.setDescription("TestDescription");
        newTransactionRequest.setFee(TRANSACTION_FEE);
        newTransactionRequest.setAmount(TRANSACTION_AMOUNT);

        //Setup Search Transaction
        searchTransactionRequest = new SearchTransactionRequest();
        searchTransactionRequest.setIban(IBAN);
        searchTransactionRequest.setSort("asc");

        //Setup Transaction Status
        transactionStatusRequest = new TransactionStatusRequest();
        transactionStatusRequest.setReference(REFERENCE);
        transactionStatusRequest.setChannel(TransactionChannelCriteria.CLIENT.getChannel());

        //Setup BankAccount
        bankAccount = new BankAccount();
        bankAccount.setBalance(ACCOUNT_BALANCE);
        bankAccount.setIban(IBAN);

        //Setup Transaction
        transaction = new Transaction();
        transaction.setReference(REFERENCE);
        transaction.setIBAN(IBAN);
        transaction.setDate(new Date());
        transaction.setDescription("TestDescription");
        transaction.setFee(TRANSACTION_FEE);
        transaction.setAmount(TRANSACTION_AMOUNT);
    }

    @Test
    void createTransaction_withAllParameters_returnsReference(){
        //Setup
        newTransactionRequest.setReference("newTransaction");
        transaction.setReference("newTransaction");
        when(bankAccountControllerMock.findAccount(newTransactionRequest.getIBAN())).thenReturn(bankAccount);
        //Test
        String transactionString = testee.createTransaction(newTransactionRequest);
        //Verify
        verify(transactionDaoMock, times(1)).save(transaction);
        Assertions.assertEquals(newTransactionRequest.getReference(),transactionString);
    }

    @Test
    void createTransaction_withoutReference_returnsUUIDReference(){
        //Setup
        newTransactionRequest.setReference(null);
        when(bankAccountControllerMock.findAccount(newTransactionRequest.getIBAN())).thenReturn(bankAccount);
        //Test
        String transactionString = testee.createTransaction(newTransactionRequest);
        //Verify
        verify(transactionDaoMock, times(1)).save(any(Transaction.class));
        Assertions.assertTrue(UUID_REGEX.matcher(transactionString).matches());
    }

    @Test
    void createTransaction_IBANDoesNotExist_throwsException(){
        //Setup
        when(bankAccountControllerMock.findAccount(newTransactionRequest.getIBAN())).thenReturn(null);
        //Test
        assertThrows(InvalidInputException.class, () -> testee.createTransaction(newTransactionRequest));
        //Verify
        verify(transactionDaoMock, times(0)).save(transaction);
    }

    @Test
    void createTransaction_makesAccountBalanceBelowZero_throwsException(){
        //Setup
        newTransactionRequest.setAmount(-ACCOUNT_BALANCE -1.0);
        when(bankAccountControllerMock.findAccount(newTransactionRequest.getIBAN())).thenReturn(bankAccount);
        //Test
        assertThrows(InvalidInputException.class, () -> testee.createTransaction(newTransactionRequest));
        //Verify
        verify(transactionDaoMock, times(0)).save(transaction);
    }

    @Test
    void showTransaction_withoutParameters_showLatestTransactionsAscendingByAmount() {
        //Setup
        searchTransactionRequest.setIban(null);

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);

        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        when(transactionDaoMock.findAll(any(PageRequest.class))).thenReturn(transactionPage);
        //Test
        List<Transaction> result = testee.showTransactions(searchTransactionRequest);
        //Verify
        verify(transactionDaoMock, times(1)).findAll(any(PageRequest.class));
        Assertions.assertEquals(transactionList, result);
    }

    @Test
    void showTransaction_withIban_showTransactionsAscendingByAmount() {
        //Setup
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        when(transactionDaoMock.findAllByIBANEqualsOrderByAmountAsc(searchTransactionRequest.getIban()))
                .thenReturn(transactionList);
        //Test
        List<Transaction> result = testee.showTransactions(searchTransactionRequest);
        //Verify
        verify(transactionDaoMock, times(1))
                .findAllByIBANEqualsOrderByAmountAsc(searchTransactionRequest.getIban());
        Assertions.assertEquals(transactionList, result);
    }

    @Test
    void showTransaction_withIbanAndSortingDesc_showTransactionsDescendingByAmount() {
        //Setup
        searchTransactionRequest.setSort("desc");

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        when(transactionDaoMock.findAllByIBANEqualsOrderByAmountDesc(searchTransactionRequest.getIban()))
                .thenReturn(transactionList);
        //Test
        List<Transaction> result = testee.showTransactions(searchTransactionRequest);
        //Verify
        verify(transactionDaoMock, times(1))
                .findAllByIBANEqualsOrderByAmountDesc(searchTransactionRequest.getIban());
        Assertions.assertEquals(transactionList, result);
    }

    @Test
    void findTransactionStatus_withInvalidReference_returnsInvalidTransactionStatusResponse() {
        //Setup
        Optional<Transaction> optionalEmptyTransaction = Optional.empty();

        when(transactionDaoMock.findById(transactionStatusRequest.getReference())).thenReturn(optionalEmptyTransaction);
        //Test
        TransactionStatusResponse result = testee.findTransactionStatus(transactionStatusRequest);
        //Verify
        Assertions.assertEquals(result.getStatus(), TransactionStatusCriteria.INVALID.getStatus());
    }

    @ParameterizedTest
    @MethodSource("setChannelAndDateTransactionStatusRequest")
    void findTransactionStatus_withDateBeforeToday_returnsTransactionStatusResponse
            (String date, TransactionChannelCriteria channelRequest, TransactionStatusCriteria statusExpected,
             Double expectedAmount, Double expectedFee)
            throws ParseException {
        //Setup
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        transaction.setDate(dateFormat.parse(date));
        Optional<Transaction> optionalTransaction = Optional.of(transaction);
        transactionStatusRequest.setChannel(channelRequest.getChannel());

        when(transactionDaoMock.findById(transactionStatusRequest.getReference())).thenReturn(optionalTransaction);
        //Test
        TransactionStatusResponse result = testee.findTransactionStatus(transactionStatusRequest);
        //Verify
        Assertions.assertEquals(result.getStatus(), statusExpected.getStatus());
        Assertions.assertEquals(result.getAmount(), expectedAmount);
        Assertions.assertEquals(result.getFee(), expectedFee);
    }

    private static Stream<Arguments> setChannelAndDateTransactionStatusRequest(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String today = dateFormat.format(date);
        Double amountSubFee = TRANSACTION_AMOUNT - TRANSACTION_FEE;
        Double zeroFee = null;

        return Stream.of(
                Arguments.of(
                        "2000-08-21 12:42:12",
                        TransactionChannelCriteria.CLIENT,
                        TransactionStatusCriteria.SETTLED,
                        amountSubFee,
                        zeroFee
                ),
                Arguments.of(
                        "2000-08-21 12:42:12",
                        TransactionChannelCriteria.ATM,
                        TransactionStatusCriteria.SETTLED,
                        amountSubFee,
                        zeroFee
                ),
                Arguments.of(
                        "2000-08-21 12:42:12",
                        TransactionChannelCriteria.INTERNAL,
                        TransactionStatusCriteria.SETTLED,
                        TRANSACTION_AMOUNT,
                        TRANSACTION_FEE
                ),
                Arguments.of(
                        today,
                        TransactionChannelCriteria.CLIENT,
                        TransactionStatusCriteria.PENDING,
                        amountSubFee,
                        zeroFee
                ),
                Arguments.of(
                        today,
                        TransactionChannelCriteria.ATM,
                        TransactionStatusCriteria.PENDING,
                        amountSubFee,
                        zeroFee
                ),
                Arguments.of(
                        today,
                        TransactionChannelCriteria.INTERNAL,
                        TransactionStatusCriteria.PENDING,
                        TRANSACTION_AMOUNT,
                        TRANSACTION_FEE
                ),
                Arguments.of(
                        "2999-08-21 12:42:12",
                        TransactionChannelCriteria.CLIENT,
                        TransactionStatusCriteria.FUTURE,
                        amountSubFee,
                        zeroFee
                ),
                Arguments.of(
                        "2999-08-21 12:42:12",
                        TransactionChannelCriteria.INTERNAL,
                        TransactionStatusCriteria.FUTURE,
                        TRANSACTION_AMOUNT,
                        TRANSACTION_FEE
                ),
                Arguments.of(
                        "2999-08-21 12:42:12",
                        TransactionChannelCriteria.ATM,
                        TransactionStatusCriteria.PENDING,
                        amountSubFee,
                        zeroFee
                )
        );
    }

}
