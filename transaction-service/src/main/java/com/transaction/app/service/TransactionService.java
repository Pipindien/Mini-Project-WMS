package com.transaction.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.dto.TransactionList;
import com.transaction.app.dto.TransactionRequest;
import com.transaction.app.dto.TransactionResponse;
import com.transaction.app.entity.Transaction;

import java.util.List;

public interface TransactionService {

    TransactionResponse buyTransaction(TransactionRequest transactionRequest, String token) throws JsonProcessingException;

    TransactionResponse updateTransaction(TransactionRequest request, String trxNumber, String token) throws JsonProcessingException;

    List<TransactionList> getTransactionStatus(String status);

    Transaction getTrxNumber(String trxNumber);

    Transaction getTransactionNumber(String trxNumber) throws JsonProcessingException;

    List<TransactionResponse> getTransactionsByCustId(String token);

    List<TransactionResponse> getTransactionsByGoalName(String token, String goalName);


}
