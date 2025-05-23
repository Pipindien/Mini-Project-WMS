package com.transaction.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.dto.TransactionList;
import com.transaction.app.dto.TransactionRequest;
import com.transaction.app.dto.TransactionResponse;
import com.transaction.app.entity.Transaction;
import com.transaction.app.service.PortfolioSummaryService;
import com.transaction.app.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    private PortfolioSummaryService portfolioSummaryService;


    @PostMapping("/buy")
    public ResponseEntity<TransactionResponse> saveTransaction(
            @Valid @RequestBody TransactionRequest transactionRequest,
            @RequestHeader String token) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.buyTransaction(transactionRequest, token));
    }

    @PutMapping("/update/{trxNumber}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @Valid @RequestBody TransactionRequest request,
            @PathVariable String trxNumber,
            @RequestHeader String token) throws JsonProcessingException {

        TransactionResponse response = transactionService.updateTransaction(request, trxNumber, token);

        // Update portfolio & progress setelah transaksi di-update
        Long goalId = response.getGoalId();
        portfolioSummaryService.upsertPortfolioSummary(goalId, token);
        portfolioSummaryService.updateProgress(goalId, token);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{trxNumber}")
    public ResponseEntity<TransactionResponse> getTransactionNumber(
            @PathVariable String trxNumber,
            @RequestHeader("token") String token) throws JsonProcessingException {
        return ResponseEntity.ok(transactionService.getTransactionNumber(trxNumber, token));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionList>> getTransactionByStatusAndCust(
            @RequestHeader String token,
            @RequestParam("status") String status) {

        return ResponseEntity.ok(transactionService.getTransactionStatusAndCust(status, token));
    }


    @PostMapping("/sell")
    public ResponseEntity<List<TransactionResponse>> sellByProductName(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader String token
    ) throws JsonProcessingException {
        String productName = (String) requestBody.get("productName");
        int lot = (Integer) requestBody.get("lot");

        // Periksa jika goalId bukan null dan konversi dengan aman
        Object goalIdObj = requestBody.get("goalId");
        Long goalId = null;
        if (goalIdObj instanceof String) {
            goalId = Long.parseLong((String) goalIdObj); // Mengonversi dari String ke Long
        } else if (goalIdObj instanceof Integer) {
            goalId = ((Integer) goalIdObj).longValue(); // Mengonversi dari Integer ke Long
        } else if (goalIdObj instanceof Long) {
            goalId = (Long) goalIdObj; // Jika sudah Long, tidak perlu konversi
        }

        if (goalId == null) {
            return ResponseEntity.badRequest().body(null); // Mengembalikan response bad request jika goalId tidak valid
        }

        // Panggil service dengan parameter goalId
        List<TransactionResponse> responses = transactionService.sellByProductName(productName, lot, goalId, token);

        // Ambil goalId yang terdampak dan update portfolio
        Set<Long> affectedGoals = responses.stream()
                .map(TransactionResponse::getGoalId)
                .collect(Collectors.toSet());

        for (Long affectedGoalId : affectedGoals) {
            portfolioSummaryService.upsertPortfolioSummary(affectedGoalId, token);
            portfolioSummaryService.updateProgress(affectedGoalId, token);
        }

        return ResponseEntity.ok(responses);
    }

}