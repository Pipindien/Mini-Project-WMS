package com.transaction.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.app.advice.exception.*;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
            throw new ProductNotFoundException("Product tidak ditemukan: " + transactionRequest.getProductName());
        }

        FinancialGoalResponse financialGoalResponse = new FinancialGoalResponse();
        financialGoalResponse.setGoalName(transactionRequest.getGoalName());

        FinancialGoalResponse financialGoalRequest = fingolClient.getFinansialGoalByName(financialGoalResponse, token);

        if (financialGoalRequest == null || !financialGoalRequest.getCustId().equals(custId)) {
            throw new GoalNotFoundException("Financial Goal tidak ditemukan atau bukan milik user: " + transactionRequest.getGoalName());
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
            throw new ProductNotFoundException("Product tidak ditemukan: " + request.getProductName());
        }

        Transaction existingTransaction = getTrxNumber(trxNumber);
        if (existingTransaction == null) {
            throw new TrxNumberNotFoundException("Invalid Trx Number: " + trxNumber);
        }

        FinancialGoalResponse financialGoalResponse = new FinancialGoalResponse();
        financialGoalResponse.setGoalName(request.getGoalName());

        FinancialGoalResponse financialGoalRequest = fingolClient.getFinansialGoalByName(financialGoalResponse, token);

        if (financialGoalRequest == null || !financialGoalRequest.getCustId().equals(custId)) {
            throw new GoalNotFoundException("Financial Goal tidak ditemukan atau bukan milik user: " + request.getGoalName());
        }


        GopayResponse gopayResponse = new GopayResponse();
        GopayResponse gopayRequest = gopayClient.getGopayStatus(gopayResponse);
        if (gopayRequest == null || gopayRequest.getPhone() == null) {
            throw new PhoneNotFoundException("Phone tidak ditemukan untuk user dengan ID: " + custId);
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
    public TransactionResponse updateTransactionStatusOnly(String trxNumber, String status, String token) throws JsonProcessingException {
        Long custId = usersClient.getIdCustFromToken(token);

        Transaction transaction = getTrxNumber(trxNumber);
        if (transaction == null) {
            throw new TrxNumberNotFoundException("Transaksi tidak ditemukan dengan nomor: " + trxNumber);
        }

        transaction.setStatus(status);
        transaction.setUpdateDate(new Date());
        Transaction savedTransaction = transactionRepository.save(transaction);

        TransactionHistory history = new TransactionHistory();
        history.setTransaction(transaction);
        history.setStatus(status);
        history.setCustId(transaction.getCustId());
        history.setProductId(transaction.getProductId());
        history.setAmount(transaction.getAmount());
        history.setCreatedDate(new Date());
        history.setNotes("Status updated via PaymentService");
        history.setLot(transaction.getLot());
        history.setGoalId(transaction.getGoalId());
        transactionHistoryRepository.save(history);

        return TransactionResponse.builder()
                .status(savedTransaction.getStatus())
                .amount(savedTransaction.getAmount())
                .custId(savedTransaction.getCustId())
                .productId(savedTransaction.getProductId())
                .productPrice(savedTransaction.getProductPrice())
                .lot(savedTransaction.getLot())
                .goalId(savedTransaction.getGoalId())
                .notes("Status updated via PaymentService")
                .build();
    }

    @Override
    public List<TransactionResponse> sellByProductName(String productName, int lotToSell, String token) throws JsonProcessingException {
        Long custId = usersClient.getIdCustFromToken(token);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName(productName);
        ProductRequest product = productClient.getProductByProductByName(productRequest);
        if (product == null) {
            throw new ProductNotFoundException("Produk tidak ditemukan: " + productName);
        }

        List<Transaction> transactions = transactionRepository.findByCustIdAndProductIdAndStatusOrderByCreatedDateAsc(
                custId, product.getProductId(), "SUCCESS");

        if (transactions.isEmpty()) {
            throw new TrxNumberNotFoundException("Tidak ada transaksi aktif untuk produk: " + productName);
        }

        List<TransactionResponse> responses = new ArrayList<>();
        int remainingLot = lotToSell;

        for (Transaction trx : transactions) {
            if (remainingLot <= 0) break;

            int availableLot = trx.getLot();
            int lotToProcess = Math.min(availableLot, remainingLot);
            remainingLot -= lotToProcess;

            // === Perhitungan bunga harian ===
            long daysDiff = ChronoUnit.DAYS.between(
                    trx.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    LocalDate.now()
            );
            double rate = product.getProductRate(); // rate bulanan, misal 0.01 untuk 1%
            double dailyRate = rate / 30.0; // asumsikan 1 bulan = 30 hari

            double sellPricePerLot = trx.getProductPrice() * Math.pow(1 + dailyRate, daysDiff);
            double totalSellAmount = sellPricePerLot * lotToProcess;

            // Simpan riwayat penjualan
            TransactionHistory sellHistory = new TransactionHistory();
            sellHistory.setTransaction(trx);
            sellHistory.setStatus("sold");
            sellHistory.setCustId(custId);
            sellHistory.setProductId(trx.getProductId());
            sellHistory.setAmount(totalSellAmount);
            sellHistory.setCreatedDate(new Date());
            sellHistory.setNotes("Sell via productName");
            sellHistory.setLot(lotToProcess);
            sellHistory.setGoalId(trx.getGoalId());
            transactionHistoryRepository.save(sellHistory);

            trx.setLot(availableLot - lotToProcess);
            trx.setUpdateDate(new Date());
            if (trx.getLot() == 0) {
                trx.setStatus("sold");
            }

            transactionRepository.save(trx);

            TransactionResponse response = TransactionResponse.builder()
                    .status("SUCCESS")
                    .amount(totalSellAmount)
                    .custId(custId)
                    .productId(trx.getProductId())
                    .productPrice(sellPricePerLot)
                    .lot(lotToProcess)
                    .goalId(trx.getGoalId())
                    .notes("Sell via productName")
                    .build();
            responses.add(response);
        }

        if (remainingLot > 0) {
            throw new LotException("Jumlah lot tidak mencukupi untuk produk " + productName);
        }

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_SELL,
                mapper.writeValueAsString(productName + " - " + lotToSell),
                mapper.writeValueAsString(responses),
                "Sell transaction by product name"
        );

        return responses;
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

    public List<TransactionResponse> getTransactionsByCustId(String token) throws JsonProcessingException {
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

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_GET_TRX_NUMBER,
                mapper.writeValueAsString(custId),
                mapper.writeValueAsString(responses),
                "Insert Get My Transaction"
        );

        return responses;
    }

    @Override
    public List<TransactionResponse> getTransactionsByGoalName(String token, String goalName) throws JsonProcessingException {
        Long custId = usersClient.getIdCustFromToken(token);

        FinancialGoalResponse goalRequest = new FinancialGoalResponse();
        goalRequest.setGoalName(goalName);

        FinancialGoalResponse goalResponse = fingolClient.getFinansialGoalByName(goalRequest, token);
        if (goalResponse == null) {
            throw new GoalNotFoundException("Goal tidak ditemukan: " + goalName);
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

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_GET_TRX_NUMBER,
                mapper.writeValueAsString(custId),
                mapper.writeValueAsString(responses),
                "Insert Get My Transaction And Goal Name"
        );
        return responses;
    }
}