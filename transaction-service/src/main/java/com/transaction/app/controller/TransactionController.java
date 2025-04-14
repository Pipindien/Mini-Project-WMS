package com.transaction.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.dto.TransactionList;
import com.transaction.app.dto.TransactionRequest;
import com.transaction.app.dto.TransactionResponse;
import com.transaction.app.entity.Transaction;
import com.transaction.app.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;


    @PostMapping("/buy")
    public ResponseEntity<TransactionResponse> saveTransaction(
            @Valid @RequestBody TransactionRequest transactionRequest,
            @RequestHeader String token) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.buyTransaction(transactionRequest, token));
    }

    @PutMapping("/update/{trxNumber}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @Valid  @RequestBody TransactionRequest request,
            @PathVariable String trxNumber,
            @RequestHeader String token) throws JsonProcessingException {
        return ResponseEntity.ok(transactionService.updateTransaction(request, trxNumber, token));
    }

    @PutMapping("/update-status/{trxNumber}")
    public ResponseEntity<String> updateTransactionStatusOnly(
            @PathVariable String trxNumber,
            @RequestBody Map<String, String> request,
            @RequestHeader String token) throws JsonProcessingException {

        String status = request.get("status");
        transactionService.updateTransactionStatusOnly(trxNumber, status, token);
        return ResponseEntity.ok("Status transaction berhasil diupdate ke: " + status);
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

    @GetMapping("/my")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(@RequestHeader String token) throws JsonProcessingException {
        List<TransactionResponse> response = transactionService.getTransactionsByCustId(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my/{goalName}")
    public ResponseEntity<List<TransactionResponse>> getMyTransactionsByGoalName(
            @RequestHeader String token,
            @PathVariable String goalName) throws JsonProcessingException {
        List<TransactionResponse> response = transactionService.getTransactionsByGoalName(token, goalName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sell")
    public ResponseEntity<List<TransactionResponse>> sellByProductName(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader String token
    ) throws JsonProcessingException {
        String productName = (String) requestBody.get("productName");
        int lot = (Integer) requestBody.get("lot");

        List<TransactionResponse> responses = transactionService.sellByProductName(productName, lot, token);
        return ResponseEntity.ok(responses);
    }



}
