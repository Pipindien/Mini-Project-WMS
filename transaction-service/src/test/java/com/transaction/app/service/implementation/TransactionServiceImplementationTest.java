package com.transaction.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.app.client.*;
import com.transaction.app.client.dto.*;
import com.transaction.app.constant.GeneralConstant;
import com.transaction.app.dto.TransactionRequest;
import com.transaction.app.dto.TransactionResponse;
import com.transaction.app.entity.Transaction;
import com.transaction.app.entity.TransactionHistory;
import com.transaction.app.repository.TransactionHistoryRepository;
import com.transaction.app.repository.TransactionRepository;
import com.transaction.app.service.AuditTrailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceImplementationTest {

    @InjectMocks
    private TransactionServiceImplementation service;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private UsersClient usersClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private FingolClient fingolClient;

    @Mock
    private GopayClient gopayClient;

    @Mock
    private AuditTrailsService auditTrailsService;

    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuyTransactionSuccess() throws JsonProcessingException {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setProductName("Product A");
        request.setGoalName("Goal A");
        request.setAmount(100000.0);
        request.setStatus("SUCCESS");
        request.setNotes("Some note");

        String token = "mock-token";
        Long custId = 123L;

        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductId(1L);
        productRequest.setProductPrice(50000.0);

        FinancialGoalResponse goalResponse = new FinancialGoalResponse();
        goalResponse.setGoalId(10L);
        goalResponse.setCustId(custId);

        GopayResponse gopayResponse = new GopayResponse();
        gopayResponse.setStatus("SUCCESS");

        when(usersClient.getIdCustFromToken(token)).thenReturn(custId);
        when(productClient.getProductByProductByName(any())).thenReturn(productRequest);
        when(fingolClient.getFinansialGoalByName(any(), eq(token))).thenReturn(goalResponse);
        when(gopayClient.getGopayTransaction(any())).thenReturn(gopayResponse);

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setTrxNumber("TRX-123");
        savedTransaction.setStatus("SUCCESS");
        savedTransaction.setAmount(100000.0);
        savedTransaction.setLot(2);
        savedTransaction.setProductId(1L);
        savedTransaction.setCustId(custId);
        savedTransaction.setGoalId(10L);
        savedTransaction.setCreatedDate(new Date());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionHistoryRepository.save(any(TransactionHistory.class))).thenReturn(null);

        // Act
        TransactionResponse response = service.buyTransaction(request, token);

        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(100000.0, response.getAmount());
        assertEquals(2, response.getLot());
        verify(auditTrailsService, times(2)).logsAuditTrails(any(), any(), any(), any());
    }

    @Test
    void testUpdateTransactionSuccess() throws JsonProcessingException {
        String token = "token";
        Long custId = 1L;

        TransactionRequest request = new TransactionRequest();
        request.setAmount(100000.0);
        request.setNotes("Updated");

        Transaction existingTransaction = new Transaction();
        existingTransaction.setTrxNumber("TRX-001");
        existingTransaction.setCustId(custId);
        existingTransaction.setProductId(1L);
        existingTransaction.setGoalId(10L);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductId(1L);
        productResponse.setProductPrice(50000.0);

        GopayResponse gopayResponse = new GopayResponse();
        gopayResponse.setStatus("SUCCESS");

        when(usersClient.getIdCustFromToken(token)).thenReturn(custId);
        when(transactionRepository.findByTrxNumber("TRX-001")).thenReturn(existingTransaction);
        when(gopayClient.getGopayStatus(any())).thenReturn(gopayResponse);
        when(productClient.getProductById(1L)).thenReturn(productResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(existingTransaction);

        TransactionResponse response = service.updateTransaction(request, "TRX-001", token);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(100000.0, response.getAmount());
        verify(transactionHistoryRepository).save(any(TransactionHistory.class));
    }

    @Test
    void testBuyTransactionProductNotFound() {
        String token = "token";
        TransactionRequest request = new TransactionRequest();
        request.setProductName("InvalidProduct");

        when(usersClient.getIdCustFromToken(token)).thenReturn(1L);
        when(productClient.getProductByProductByName(any())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> service.buyTransaction(request, token));
    }
}
