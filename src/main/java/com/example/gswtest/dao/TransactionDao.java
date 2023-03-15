package com.example.gswtest.dao;

import com.example.gswtest.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDao extends JpaRepository<Transaction, String> {
    List<Transaction> findAllByIBANEqualsOrderByAmountAsc(String IBAN);
    List<Transaction> findAllByIBANEqualsOrderByAmountDesc(String IBAN);
}
