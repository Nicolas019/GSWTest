package com.example.gswtest.controller;

import com.example.gswtest.constant.TransactionChannelCriteria;
import com.example.gswtest.constant.TransactionStatusCriteria;
import com.example.gswtest.dao.TransactionDao;
import com.example.gswtest.exception.InvalidInputException;
import com.example.gswtest.model.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class TransactionController {
    private final TransactionDao transactionDao;
    private final BankAccountController bankAccountController;

    public TransactionController(TransactionDao transactionDao, BankAccountController bankAccountController) {
        this.transactionDao = transactionDao;
        this.bankAccountController = bankAccountController;
    }

    public String createTransaction(NewTransactionRequest transactionRequest) throws InvalidInputException{
        checkTransaction(transactionRequest);

        Transaction transaction = new Transaction();
        //TODO Assert there is no duplicates with that UUID
        String reference = transactionRequest.getReference() == null ? String.valueOf(UUID.randomUUID()) : transactionRequest.getReference();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDate(transactionRequest.getDate() == null ? new Date() : transactionRequest.getDate());
        transaction.setIBAN(transactionRequest.getIBAN());
        transaction.setFee(transactionRequest.getFee() == null ? 0 : transactionRequest.getFee());
        transaction.setReference(reference);
        transaction.setDescription(transactionRequest.getDescription() == null ? "" : transactionRequest.getDescription());
        transactionDao.save(transaction);

        return reference;
    }

    private void checkTransaction(NewTransactionRequest transactionRequest) throws InvalidInputException {
        //Cleaning TransactionRequest
        BankAccount account = bankAccountController.findAccount(transactionRequest.getIBAN());
        Double fee = transactionRequest.getFee() == null ? 0 : transactionRequest.getFee();

        //Checking requirements
        if(account == null){
            throw new InvalidInputException(String.format("Account '%s' not found for transaction '%s'", transactionRequest.getIBAN(), transactionRequest.getDescription()));
        } else if (account.getBalance() + (transactionRequest.getAmount() - fee) < 0) {
            throw new InvalidInputException(String.format("Account '%s' has not have enough credit %f for transaction '%s' with amount %f", transactionRequest.getIBAN(), account.getBalance(), transactionRequest.getDescription(), transactionRequest.getAmount()));
        }
    }

    //It will sort by asc amount as default
    public List<Transaction> showTransactions(SearchTransactionRequest request) {
        if(request.getIban() != null) {
            if(request.getSort() != null && request.getSort().equalsIgnoreCase("desc")){
                return transactionDao.findAllByIBANEqualsOrderByAmountDesc(request.getIban());
            }
            return transactionDao.findAllByIBANEqualsOrderByAmountAsc(request.getIban());
        } else {
            return transactionDao.findAll(
                    PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "date"))
            ).stream().toList();
        }
    }

    public TransactionStatusResponse findTransactionStatus(TransactionStatusRequest request) {
        TransactionStatusResponse response = new TransactionStatusResponse();
        Optional<Transaction> optionalTransaction = transactionDao.findById(request.getReference());
        if(optionalTransaction.isEmpty()){
            response.setStatus(TransactionStatusCriteria.INVALID.getStatus());
            response.setReference(request.getReference());
            return response;
        }

        Transaction transaction = optionalTransaction.get();
        TransactionChannelCriteria channelCriteria = TransactionChannelCriteria.getByChannel(request.getChannel());
        response.setReference(request.getReference());


        ZoneId zone = ZoneId.of("UTC");
        LocalDate now = LocalDate.now(zone);
        LocalDate requestTime = transaction.getDate().toInstant().atZone(zone).toLocalDate();

        if (requestTime.equals(now)) {

            if(channelCriteria == TransactionChannelCriteria.ATM || channelCriteria == TransactionChannelCriteria.CLIENT) {
                response.setStatus(TransactionStatusCriteria.PENDING.getStatus());
                response.setAmount(transaction.getAmount() - transaction.getFee());
            } else {
                response.setStatus(TransactionStatusCriteria.PENDING.getStatus());
                response.setAmount(transaction.getAmount());
                response.setFee(transaction.getFee());
            }

        } else if (requestTime.isAfter(now)) {

            if(channelCriteria == TransactionChannelCriteria.ATM) {
                response.setStatus(TransactionStatusCriteria.PENDING.getStatus());
                response.setAmount(transaction.getAmount() - transaction.getFee());
            } else if (channelCriteria == TransactionChannelCriteria.CLIENT) {
                response.setStatus(TransactionStatusCriteria.FUTURE.getStatus());
                response.setAmount(transaction.getAmount() - transaction.getFee());
            } else {
                response.setStatus(TransactionStatusCriteria.FUTURE.getStatus());
                response.setAmount(transaction.getAmount());
                response.setFee(transaction.getFee());
            }

        } else { //requestTime.isBefore(now)

            if(channelCriteria == TransactionChannelCriteria.ATM || channelCriteria == TransactionChannelCriteria.CLIENT) {
                response.setStatus(TransactionStatusCriteria.SETTLED.getStatus());
                response.setAmount(transaction.getAmount() - transaction.getFee());
            } else {
                response.setStatus(TransactionStatusCriteria.SETTLED.getStatus());
                response.setAmount(transaction.getAmount());
                response.setFee(transaction.getFee());
            }
        }


        return response;
    }
}
