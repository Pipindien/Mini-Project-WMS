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
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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

        // Ambil productPrice dari ProductClient berdasarkan productId
        Long productId = existingTransaction.getProductId();
        ProductResponse product = productClient.getProductById(productId);
        if (product == null) {
            throw new ProductNotFoundException("Produk tidak ditemukan berdasarkan ID: " + productId);
        }

        // Hitung ulang lot
        int lot = (int) (request.getAmount() / product.getProductPrice());

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
    public List<TransactionResponse> sellByProductName(String productName, int lotToSell, Long goalId, String token) throws JsonProcessingException {
        Long custId = usersClient.getIdCustFromToken(token);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName(productName);
        ProductRequest productInfo = productClient.getProductByProductByName(productRequest);
        if (productInfo == null) {
            throw new ProductNotFoundException("Produk tidak ditemukan: " + productName);
        }

        List<Transaction> transactions = transactionRepository.findByCustIdAndProductIdAndGoalIdAndStatusOrderByCreatedDateAsc(
                custId, productInfo.getProductId(), goalId,"SUCCESS");

        if (transactions.isEmpty()) {
            throw new TrxNumberNotFoundException("Tidak ada transaksi aktif untuk produk: " + productName);
        }

        List<Transaction> filteredTransactions = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getCreatedDate))
                .filter(trx -> trx.getLot() > 0)
                .collect(Collectors.toList());

        List<TransactionResponse> responses = new ArrayList<>();
        int remainingLot = lotToSell;

        for (Transaction trx : filteredTransactions) {
            if (remainingLot <= 0) break;

            int availableLot = trx.getLot();
            int lotToProcess = Math.min(availableLot, remainingLot);
            remainingLot -= lotToProcess;

            ProductResponse currentProduct = productClient.getProductById(trx.getProductId());
            if (currentProduct == null) {
                throw new ProductNotFoundException("Produk tidak ditemukan berdasarkan ID: " + trx.getProductId());
            }

            // === Kalkulasi bunga harian ===
            double productPrice = currentProduct.getProductPrice();
            double investmentAmount = productPrice * lotToProcess;
            int nDays = (int) DateHelper.calculateDayDiff(trx.getCreatedDate(), LocalDate.now());

            double monthlyRate = currentProduct.getProductRate(); // rate bulanan
            double dailyRate = Math.pow(1 + monthlyRate, 1.0 / 30) - 1;
            double multiplier = Math.pow(1 + dailyRate, nDays);
            double estimatedReturn = investmentAmount * multiplier;
            double sellPricePerLot = estimatedReturn / lotToProcess; // Untuk response info
            double totalSellAmount = estimatedReturn;

            // Simpan riwayat penjualan
            TransactionHistory sellHistory = new TransactionHistory();
            sellHistory.setTransaction(trx);
            sellHistory.setStatus("SOLD");
            sellHistory.setCustId(custId);
            sellHistory.setProductId(trx.getProductId());
            sellHistory.setAmount(totalSellAmount);
            sellHistory.setCreatedDate(new Date());
            sellHistory.setNotes("Sell via productName");
            sellHistory.setLot(lotToProcess);
            sellHistory.setGoalId(trx.getGoalId());
            transactionHistoryRepository.save(sellHistory);

            // Update transaksi
            trx.setLot(availableLot - lotToProcess);
            trx.setUpdateDate(new Date());
            if (trx.getLot() == 0) {
                trx.setStatus("SOLD");
            }
            transactionRepository.save(trx);

            // Buat response
            TransactionResponse response = TransactionResponse.builder()
                    .status("SUCCESS")
                    .amount(totalSellAmount)
                    .custId(custId)
                    .productId(trx.getProductId())
                    .productName(currentProduct.getProductName())
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
    public List<TransactionList> getTransactionStatusAndCust(String status, String token) {
        Long custId = usersClient.getIdCustFromToken(token);

        List<TransactionList> transactions = transactionHistoryRepository
                .findTransactionListByStatusAndCustId(status, custId);

        if (transactions == null || transactions.isEmpty()) {
            return Collections.emptyList();
        }

        for (TransactionList trx : transactions) {


            ProductResponse product = productClient.getProductById(trx.getProductId());
            if (product != null) {
                trx.setProductName(product.getProductName());
                trx.setProductPrice(product.getProductPrice());
            }


            FinancialGoalResponse goal = fingolClient.getFinancialGoalByIdWithOutDelete(trx.getGoalId(), token);
            if (goal != null) {
                trx.setGoalName(goal.getGoalName());
            }
        }

        return transactions;
    }

    @Override
    public Transaction getTrxNumber(String trxNumber) {
        return transactionRepository.findByTrxNumber(trxNumber);
    }

    @Override
    public TransactionResponse getTransactionNumber(String trxNumber, String token) throws JsonProcessingException {
        Transaction transaction = transactionRepository.findTransactionByTrxNumber(trxNumber);

        String productName = null;
        Double productPrice = null;
        if (transaction.getProductId() != null) {
            ProductResponse product = productClient.getProductById(transaction.getProductId());
            if (product != null) {
                productName = product.getProductName();
                productPrice = product.getProductPrice();
            }
        }

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
        response.setProductPrice(productPrice);
        response.setProductName(productName);
        response.setGoalId(transaction.getGoalId());
        response.setGoalName(goalName);

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_GET_TRX_NUMBER,
                mapper.writeValueAsString(transaction),
                mapper.writeValueAsString(response),
                "Insert Get TRXNumber"
        );

        return response;
    }

}