package com.transaction.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.dto.TransactionList;
import com.transaction.app.dto.TransactionRequest;
import com.transaction.app.dto.TransactionResponse;
import com.transaction.app.entity.Transaction;
import com.transaction.app.service.implementation.TransactionServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionServiceImplementation transactionService;

    @PostMapping("/buy")
    public ResponseEntity<TransactionResponse> saveTransaction(
            @RequestBody TransactionRequest transactionRequest,
            @RequestHeader String token) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.buyTransaction(transactionRequest, token));
    }

    @PutMapping("/update/{trxNumber}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @RequestBody TransactionRequest request,
            @PathVariable String trxNumber,
            @RequestHeader String token) throws JsonProcessingException {
        return ResponseEntity.ok(transactionService.updateTransaction(request, trxNumber, token));
    }

    @GetMapping("/{trxNumber}")
    public ResponseEntity<Transaction> getTransactionNumber(
            @PathVariable String trxNumber) throws JsonProcessingException {
        return ResponseEntity.ok(transactionService.getTransactionNumber(trxNumber));
    }

    @GetMapping
    public ResponseEntity<List<TransactionList>> getAllTransaction(
            @RequestParam("status") String status) {
        return ResponseEntity.ok(transactionService.getTransactionStatus(status));
    }
}
