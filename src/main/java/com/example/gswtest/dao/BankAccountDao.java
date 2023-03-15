package com.example.gswtest.dao;

import com.example.gswtest.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountDao extends JpaRepository<BankAccount, String> {
}
