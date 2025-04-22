package com.transaction.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.dto.TransactionList;
import com.transaction.app.dto.TransactionRequest;
import com.transaction.app.dto.TransactionResponse;
import com.transaction.app.entity.Transaction;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface TransactionService {

    TransactionResponse buyTransaction(TransactionRequest transactionRequest, String token) throws JsonProcessingException;

    TransactionResponse updateTransaction(TransactionRequest request, String trxNumber, String token) throws JsonProcessingException;


    List<TransactionResponse> sellByProductName(String productName, int lotToSell, Long goalId, String token) throws JsonProcessingException;


    List<TransactionList> getTransactionStatusAndCust(String status, String token);

    Transaction getTrxNumber(String trxNumber);

    TransactionResponse getTransactionNumber(String trxNumber,  String token) throws JsonProcessingException;

    List<TransactionResponse> getTransactionsByCustId(String token) throws JsonProcessingException;


}
