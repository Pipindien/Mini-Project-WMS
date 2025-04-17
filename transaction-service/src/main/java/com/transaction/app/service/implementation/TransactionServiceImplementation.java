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
import com.transaction.app.client.dto.ProductResponse;
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
import com.transaction.app.utility.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.*;

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
                .trxNumber(savedTransaction.getTrxNumber())
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

        Transaction existingTransaction = getTrxNumber(trxNumber);
        if (existingTransaction == null) {
            throw new TrxNumberNotFoundException("Invalid Trx Number: " + trxNumber);
        }

        // Cek apakah transaksi ini milik user yang sama
        if (!existingTransaction.getCustId().equals(custId)) {
            throw new RuntimeException("Transaksi bukan milik user.");
        }

        // Ambil status dari Gopay
        GopayResponse gopayRequest = gopayClient.getGopayStatus(new GopayResponse());
        if (gopayRequest == null || gopayRequest.getStatus() == null) {
            throw new RuntimeException("Gagal mengambil status Gopay");
        }

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_GOPAY_STATUS,
                mapper.writeValueAsString(new GopayResponse()),
                mapper.writeValueAsString(gopayRequest),
                "Get Gopay Status untuk update transaksi"
        );

        // Hitung ulang lot
        int lot = (int) (request.getAmount() / existingTransaction.getProductPrice());

        // Update transaksi
        existingTransaction.setStatus(gopayRequest.getStatus());
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setLot(lot);
        existingTransaction.setUpdateDate(new Date());

        Transaction savedTransaction = transactionRepository.save(existingTransaction);

        // Simpan ke history
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransaction(savedTransaction);
        transactionHistory.setCustId(custId);
        transactionHistory.setProductId(savedTransaction.getProductId());
        transactionHistory.setStatus(savedTransaction.getStatus());
        transactionHistory.setAmount(savedTransaction.getAmount());
        transactionHistory.setLot(lot);
        transactionHistory.setNotes(request.getNotes());
        transactionHistory.setCreatedDate(new Date());
        transactionHistory.setGoalId(savedTransaction.getGoalId());
        transactionHistoryRepository.save(transactionHistory);

        TransactionResponse response = TransactionResponse.builder()
                .status(savedTransaction.getStatus())
                .amount(savedTransaction.getAmount())
                .custId(savedTransaction.getCustId())
                .productId(savedTransaction.getProductId())
                .productPrice(savedTransaction.getProductPrice())
                .lot(savedTransaction.getLot())
                .goalId(savedTransaction.getGoalId())
                .build();

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_UPDATE,
                mapper.writeValueAsString(request),
                mapper.writeValueAsString(response),
                "Update transaksi oleh user"
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
            int nMonth = (int) DateHelper.calculateMonthDiff(trx.getCreatedDate(), LocalDate.now());
            double rate = product.getProductRate(); // rate bulanan, misal 0.01 untuk 1%
            double sellPricePerLot = trx.getProductPrice() * Math.pow(1 + rate, nMonth);
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
    public TransactionResponse sellByTrxNumber(String trxNumber, int lotToSell, String token) throws JsonProcessingException, AccessDeniedException {
        Long custId = usersClient.getIdCustFromToken(token);

        Transaction trx = getTrxNumber(trxNumber);
        if (trx == null) {
            throw new TrxNumberNotFoundException("Transaksi tidak ditemukan dengan nomor: " + trxNumber);
        }

        if (!trx.getCustId().equals(custId)) {
            throw new AccessDeniedException("Kamu tidak punya akses ke transaksi ini");
        }

        if (!"SUCCESS".equalsIgnoreCase(trx.getStatus()) || trx.getLot() <= 0) {
            throw new IllegalArgumentException("Transaksi tidak valid untuk dijual");
        }

        // Mengecek apakah jumlah lot yang ingin dijual tidak lebih banyak dari yang tersedia
        if (lotToSell > trx.getLot()) {
            throw new IllegalArgumentException("Jumlah lot yang ingin dijual melebihi lot yang tersedia");
        }

        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductId(trx.getProductId());
        ProductResponse product = productClient.getProductById(productRequest.getProductId());
        if (product == null) {
            throw new ProductNotFoundException("Produk tidak ditemukan untuk transaksi ini");
        }

        // === Hitung hasil penjualan ===
        int nMonth = (int) DateHelper.calculateMonthDiff(trx.getCreatedDate(), LocalDate.now());
        double rate = product.getProductRate();
        double sellPricePerLot = trx.getProductPrice() * Math.pow(1 + rate, nMonth);
        double totalSellAmount = sellPricePerLot * lotToSell;

        // Simpan riwayat penjualan
        TransactionHistory sellHistory = new TransactionHistory();
        sellHistory.setTransaction(trx);
        sellHistory.setStatus("sold");
        sellHistory.setCustId(trx.getCustId());
        sellHistory.setProductId(trx.getProductId());
        sellHistory.setAmount(totalSellAmount);
        sellHistory.setCreatedDate(new Date());
        sellHistory.setNotes("Sell via trxNumber");
        sellHistory.setLot(lotToSell);
        sellHistory.setGoalId(trx.getGoalId());
        transactionHistoryRepository.save(sellHistory);

        // Update transaksi, mengurangi lot sesuai dengan yang dijual
        trx.setLot(trx.getLot() - lotToSell);
        if (trx.getLot() == 0) {
            trx.setStatus("sold"); // Jika semua lot sudah dijual, status jadi "sold"
        }
        trx.setUpdateDate(new Date());
        transactionRepository.save(trx);

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_SELL,
                mapper.writeValueAsString("trxNumber: " + trxNumber + " - Lot: " + lotToSell),
                mapper.writeValueAsString(sellHistory),
                "Sell transaction by trx number"
        );

        return TransactionResponse.builder()
                .status("SUCCESS")
                .amount(totalSellAmount)
                .custId(trx.getCustId())
                .productId(trx.getProductId())
                .productPrice(sellPricePerLot)
                .lot(lotToSell)
                .goalId(trx.getGoalId())
                .notes("Sell via trxNumber")
                .build();
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
    public TransactionResponse getTransactionNumber(String trxNumber, String token) throws JsonProcessingException {
        Transaction transaction = transactionRepository.findTransactionByTrxNumber(trxNumber);

        // Ambil productName dari ProductClient
        String productName = null;
        if (transaction.getProductId() != null) {
            ProductResponse product = productClient.getProductById(transaction.getProductId());
            productName = product != null ? product.getProductName() : null;
        }

        // Ambil goalName dari FingolClient
        String goalName = null;
        if (transaction.getGoalId() != null) {
            FinancialGoalResponse goal = fingolClient.getFinancialGoalById(transaction.getGoalId(), token);
            goalName = goal != null ? goal.getGoalName() : null;
        }

        TransactionResponse response = new TransactionResponse();
        response.setTrxNumber(transaction.getTrxNumber());
        response.setStatus(transaction.getStatus());
        response.setAmount(transaction.getAmount());
        response.setLot(transaction.getLot());
        response.setCustId(transaction.getCustId());
        response.setProductId(transaction.getProductId());
        response.setProductPrice(transaction.getProductPrice());
        response.setProductName(productName);  // ← dari client
        response.setGoalId(transaction.getGoalId());
        response.setGoalName(goalName);        // ← dari client

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_GET_TRX_NUMBER,
                mapper.writeValueAsString(transaction),
                mapper.writeValueAsString(response),
                "Insert Get TRXNumber"
        );

        return response;
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