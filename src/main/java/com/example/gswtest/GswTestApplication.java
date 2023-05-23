package com.example.gswtest;

import com.example.gswtest.controller.TransactionController;
import com.example.gswtest.dao.BankAccountDao;
import com.example.gswtest.exception.InvalidInputException;
import com.example.gswtest.model.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("api/test")
@Log4j2
public class GswTestApplication {

    private final TransactionController controller;

    public GswTestApplication(TransactionController controller) {
        this.controller = controller;
    }

    public static void main(String[] args) {
        SpringApplication.run(GswTestApplication.class, args);
    }

    /**
     * Search for the transaction (iban) included in the request handling sorting by amount.
     */
    @PostMapping("/searchTransaction")
    public List<Transaction> searchTransaction(@RequestBody SearchTransactionRequest request) {
        return controller.showTransactions(request);
    }

    /**
     *  Will receive the transaction information in the request object and store it into the system (HSQL in memory DB).
     */
    @PostMapping("/createTransaction")
    @ResponseBody
    public String createTransaction(@RequestBody NewTransactionRequest request, HttpServletResponse response) {
        try {
            return controller.createTransaction(request);
        } catch (InvalidInputException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return e.getMessage();
        }
    }

    /**
     * Based on the request and some business rules, will return the status and additional information for a specific transaction.
     * Assumption: To know if the transaction is after, before or equal to the time been executed
     *              it will only consider the Year Month and Day, not the time.
     * @return TransactionStatusResponse
     */
    @PostMapping("/findStatus")
    @ResponseBody
    public TransactionStatusResponse findStatus(@RequestBody TransactionStatusRequest request, HttpServletResponse response) {
        try {
            return controller.findTransactionStatus(request);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

}
