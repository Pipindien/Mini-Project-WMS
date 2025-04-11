package com.transaction.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.app.client.FingolClient;
import com.transaction.app.client.GopayClient;
import com.transaction.app.client.ProductClient;
import com.transaction.app.client.UsersClient;
import com.transaction.app.client.dto.FinancialGoalResponse;
import com.transaction.app.client.dto.GopayResponse;
import com.transaction.app.client.dto.ProductRequest;
import com.transaction.app.constant.GeneralConstant;
import com.transaction.app.dto.TransactionList;
import com.transaction.app.dto.TransactionRequest;
import com.transaction.app.dto.TransactionResponse;
import com.transaction.app.entity.Transaction;
import com.transaction.app.entity.TransactionHistory;
import com.transaction.app.repository.TransactionHistoryRepository;
import com.transaction.app.repository.TransactionRepository;
import com.transaction.app.service.AuditTrailsService;
import com.transaction.app.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImplementation implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuditTrailsService auditTrailsService;

    @Autowired
    private UsersClient usersClient;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private FingolClient fingolClient;

    @Autowired
    private GopayClient gopayClient;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public TransactionResponse buyTransaction(TransactionRequest transactionRequest, String token) throws JsonProcessingException {
        Long custId = usersClient.getIdCustFromToken(token);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName(transactionRequest.getProductName());

        ProductRequest productResponse = productClient.getProductByProductByName(productRequest);
        if (productResponse == null) {
            throw new IllegalArgumentException("Product tidak ditemukan: " + transactionRequest.getProductName());
        }

        FinancialGoalResponse financialGoalResponse = new FinancialGoalResponse();
        financialGoalResponse.setGoalName(transactionRequest.getGoalName());

        FinancialGoalResponse financialGoalRequest = fingolClient.getFinansialGoalByName(financialGoalResponse, token);
        if (financialGoalRequest == null) {
            throw new IllegalArgumentException("Financial Goal tidak ditemukan: " + transactionRequest.getGoalName());
        }

        GopayResponse gopayResponse = new GopayResponse();
        gopayResponse.setAmount(transactionRequest.getAmount());
        gopayResponse.setStatus(transactionRequest.getStatus());

        GopayResponse gopayRequest = gopayClient.getGopayTransaction(gopayResponse);

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_GOPAY_TRANSACTION,
                mapper.writeValueAsString(gopayResponse),
                mapper.writeValueAsString(gopayRequest),
                "Insert Transaction Into Gopay Transaction"
        );

        int lot = (int) (transactionRequest.getAmount() / productResponse.getProductPrice());

        Transaction transaction = new Transaction();
        transaction.setTrxNumber("TRX-" + UUID.randomUUID().toString());
        transaction.setStatus(gopayRequest.getStatus());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setCustId(custId);
        transaction.setProductId(productResponse.getProductId());
        transaction.setProductPrice(productResponse.getProductPrice());
        transaction.setLot(lot);
        transaction.setGoalId(financialGoalRequest.getGoalId());
        transaction.setCreatedDate(new Date());

        Transaction savedTransaction = transactionRepository.save(transaction);

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setStatus(gopayRequest.getStatus());
        transactionHistory.setCustId(custId);
        transactionHistory.setProductId(productResponse.getProductId());
        transactionHistory.setAmount(transactionRequest.getAmount());
        transactionHistory.setCreatedDate(new Date());
        transactionHistory.setNotes(transactionRequest.getNotes());
        transactionHistory.setTransaction(savedTransaction);
        transactionHistory.setLot(lot);
        transactionHistory.setGoalId(financialGoalRequest.getGoalId());

        transactionHistoryRepository.save(transactionHistory);

        TransactionResponse response = TransactionResponse.builder()
                .status(savedTransaction.getStatus())
                .amount(savedTransaction.getAmount())
                .custId(savedTransaction.getCustId())
                .productId(savedTransaction.getProductId())
                .productPrice(savedTransaction.getProductPrice())
                .lot(savedTransaction.getLot())
                .goalId(savedTransaction.getGoalId())
                .notes(transactionRequest.getNotes())
                .build();

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_SAVE,
                mapper.writeValueAsString(transactionRequest),
                mapper.writeValueAsString(response),
                "Insert Transaction Save"
        );

        return response;
    }

    @Override
    public TransactionResponse updateTransaction(TransactionRequest request, String trxNumber, String token) throws JsonProcessingException {
        Long custId = usersClient.getIdCustFromToken(token);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName(request.getProductName());

        ProductRequest productResponse = productClient.getProductByProductByName(productRequest);
        if (productResponse == null) {
            throw new IllegalArgumentException("Product tidak ditemukan: " + request.getProductName());
        }

        Transaction existingTransaction = getTrxNumber(trxNumber);
        if (existingTransaction == null) {
            throw new IllegalArgumentException("Invalid Trx Number: " + trxNumber);
        }

        FinancialGoalResponse financialGoalRequest = null;
        if (request.getGoalName() != null) {
            FinancialGoalResponse financialGoalResponse = new FinancialGoalResponse();
            financialGoalResponse.setGoalName(request.getGoalName());

            financialGoalRequest = fingolClient.getFinansialGoalByName(financialGoalResponse, token);
            if (financialGoalRequest == null) {
                throw new IllegalArgumentException("Financial Goal tidak ditemukan: " + request.getGoalName());
            }
        }

        GopayResponse gopayResponse = new GopayResponse();
        GopayResponse gopayRequest = gopayClient.getGopayStatus(gopayResponse);
        if (gopayRequest == null || gopayRequest.getPhone() == null) {
            throw new IllegalArgumentException("Phone tidak ditemukan untuk user dengan ID: " + custId);
        }

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_GOPAY_STATUS,
                mapper.writeValueAsString(gopayResponse),
                mapper.writeValueAsString(gopayRequest),
                "Insert Transaction Into Gopay Status"
        );

        int lot = (int) (request.getAmount() / productResponse.getProductPrice());

        existingTransaction.setStatus(gopayRequest.getStatus());
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setCustId(custId);
        existingTransaction.setProductId(productResponse.getProductId());
        existingTransaction.setProductPrice(productResponse.getProductPrice());
        existingTransaction.setLot(lot);
        existingTransaction.setUpdateDate(new Date());

        if (financialGoalRequest != null) {
            existingTransaction.setGoalId(financialGoalRequest.getGoalId());
        }

        Transaction savedTransaction = transactionRepository.save(existingTransaction);

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setStatus(gopayRequest.getStatus());
        transactionHistory.setCustId(custId);
        transactionHistory.setProductId(productResponse.getProductId());
        transactionHistory.setAmount(request.getAmount());
        transactionHistory.setCreatedDate(new Date());
        transactionHistory.setNotes(request.getNotes());
        transactionHistory.setTransaction(savedTransaction);
        transactionHistory.setLot(lot);

        if (financialGoalRequest != null) {
            transactionHistory.setGoalId(financialGoalRequest.getGoalId());
        }

        transactionHistoryRepository.save(transactionHistory);

        TransactionResponse response = TransactionResponse.builder()
                .status(savedTransaction.getStatus())
                .amount(savedTransaction.getAmount())
                .custId(savedTransaction.getCustId())
                .productId(savedTransaction.getProductId())
                .productPrice(savedTransaction.getProductPrice())
                .lot(savedTransaction.getLot())
                .goalId(savedTransaction.getGoalId())
                .notes(request.getNotes())
                .build();

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_UPDATE,
                mapper.writeValueAsString(request),
                mapper.writeValueAsString(response),
                "Insert Transaction Update"
        );

        return response;
    }


    @Override
    public List<TransactionList> getTransactionStatus(String status) {
        return transactionHistoryRepository.findTransactionListByStatus(status);
    }

    @Override
    public Transaction getTrxNumber(String trxNumber) {
        return transactionRepository.findByTrxNumber(trxNumber);
    }

    @Override
    public Transaction getTransactionNumber(String trxNumber) throws JsonProcessingException {
        Transaction transaction = transactionRepository.findTransactionByTrxNumber(trxNumber);

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_GET_TRX_NUMBER,
                mapper.writeValueAsString(transaction),
                mapper.writeValueAsString(transaction),
                "Insert Get TRXNumber"
        );

        return transaction;
    }

    public List<TransactionResponse> getTransactionsByCustId(String token) {
        Long custId = usersClient.getIdCustFromToken(token);
        List<Transaction> transactions = transactionRepository.findByCustId(custId);

        List<TransactionResponse> responses = new ArrayList<>();

        for (Transaction trx : transactions) {
            TransactionResponse response = TransactionResponse.builder()
                    .status(trx.getStatus())
                    .amount(trx.getAmount())
                    .custId(trx.getCustId())
                    .productId(trx.getProductId())
                    .productPrice(trx.getProductPrice())
                    .lot(trx.getLot())
                    .goalId(trx.getGoalId())
                    .build();

            responses.add(response);
        }

        return responses;
    }

    @Override
    public List<TransactionResponse> getTransactionsByGoalName(String token, String goalName) {
        Long custId = usersClient.getIdCustFromToken(token);

        FinancialGoalResponse goalRequest = new FinancialGoalResponse();
        goalRequest.setGoalName(goalName);

        FinancialGoalResponse goalResponse = fingolClient.getFinansialGoalByName(goalRequest, token);
        if (goalResponse == null) {
            throw new IllegalArgumentException("Goal tidak ditemukan: " + goalName);
        }

        Long goalId = goalResponse.getGoalId();
        List<Transaction> transactions = transactionRepository.findByCustIdAndGoalId(custId, goalId);

        List<TransactionResponse> responses = new ArrayList<>();
        for (Transaction trx : transactions) {
            TransactionResponse response = TransactionResponse.builder()
                    .status(trx.getStatus())
                    .amount(trx.getAmount())
                    .custId(trx.getCustId())
                    .productId(trx.getProductId())
                    .productPrice(trx.getProductPrice())
                    .lot(trx.getLot())
                    .goalId(trx.getGoalId())
                    .build();
            responses.add(response);
        }

        return responses;
    }



}