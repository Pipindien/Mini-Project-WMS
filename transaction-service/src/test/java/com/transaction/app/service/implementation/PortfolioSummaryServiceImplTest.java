package com.transaction.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.app.client.FingolClient;
import com.transaction.app.client.ProductClient;
import com.transaction.app.client.UsersClient;
import com.transaction.app.client.dto.ProductResponse;
import com.transaction.app.constant.GeneralConstant;
import com.transaction.app.dto.insight.InsightResponse;
import com.transaction.app.dto.portosum.PortfolioSummaryResponse;
import com.transaction.app.entity.PortfolioSummary;
import com.transaction.app.entity.Transaction;
import com.transaction.app.repository.PortfolioSummaryRepository;
import com.transaction.app.repository.TransactionRepository;
import com.transaction.app.service.AuditTrailsService;
import com.transaction.app.service.InsightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioSummaryServiceImplTest {

    @InjectMocks
    private PortfolioSummaryServiceImpl portfolioSummaryService;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AuditTrailsService auditTrailsService;
    @Mock
    private UsersClient usersClient;
    @Mock
    private ProductClient productClient;
    @Mock
    private FingolClient fingolClient;
    @Mock
    private PortfolioSummaryRepository portfolioSummaryRepository;
    @Mock
    private InsightService insightService;

    private final ObjectMapper mapper = new ObjectMapper();

    LocalDate localDate = LocalDate.now(); // contoh LocalDate
    LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
    Date date = Date.from(tenDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void upsertPortfolioSummary() throws JsonProcessingException {
        String token = "sample-token";
        Long custId = 1L;
        Long goalId = 100L;

        Transaction trx = new Transaction();
        trx.setCustId(custId);
        trx.setGoalId(goalId);
        trx.setProductId(200L);
        trx.setLot(2);
        trx.setStatus("SUCCESS");
        trx.setCreatedDate(date);

        ProductResponse product = new ProductResponse();
        product.setProductId(200L);
        product.setProductName("Product A");
        product.setProductCategory("Category A");
        product.setProductPrice(100.0);
        product.setProductRate(0.1);

        when(usersClient.getIdCustFromToken(token)).thenReturn(custId);
        when(transactionRepository.findByCustIdAndGoalIdAndStatus(custId, goalId, "SUCCESS")).thenReturn(List.of(trx));
        when(portfolioSummaryRepository.findByCustIdAndGoalId(custId, goalId)).thenReturn(Optional.empty());
        when(portfolioSummaryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(productClient.getProductById(200L)).thenReturn(product);

        PortfolioSummaryResponse response = portfolioSummaryService.upsertPortfolioSummary(goalId, token);

        assertNotNull(response);
        assertEquals(custId, response.getCustId());
        assertEquals(goalId, response.getGoalId());
        assertFalse(response.getPortfolioProductDetails().isEmpty());

        verify(auditTrailsService).logsAuditTrails(eq(GeneralConstant.LOG_ACVITIY_UPSERTPORTFOLIO), anyString(), anyString(), anyString());
    }

    @Test
    void updateProgress() throws JsonProcessingException {
        Long goalId = 100L;
        String token = "token";

        PortfolioSummary summary = new PortfolioSummary();
        summary.setGoalId(goalId);
        summary.setEstimatedReturn(500.0);

        InsightResponse insight = new InsightResponse();
        insight.setInsightMessage("Keep going!");

        when(portfolioSummaryRepository.findOneByGoalId(goalId)).thenReturn(Optional.of(summary));
        when(insightService.generateInsight(goalId, token)).thenReturn(insight);
        when(fingolClient.updateCurrentAmountAndInsight(goalId, 500.0, "Keep going!", token)).thenReturn("updated");

        assertDoesNotThrow(() -> portfolioSummaryService.updateProgress(goalId, token));
        verify(auditTrailsService).logsAuditTrails(eq(GeneralConstant.LOG_ACVITIY_UPDATE_PROGRESS), anyString(), anyString(), anyString());
    }

    @Test
    void getPortfolioOverview() throws JsonProcessingException {
        String token = "sample-token";
        Long custId = 1L;

        PortfolioSummary summary = new PortfolioSummary();
        summary.setCustId(custId);
        summary.setEstimatedReturn(200.0);
        summary.setTotalInvestment(100.0);
        summary.setTotalProfit(100.0);

        when(usersClient.getIdCustFromToken(token)).thenReturn(custId);
        when(portfolioSummaryRepository.findAllByCustId(custId)).thenReturn(List.of(summary));

        PortfolioSummaryResponse response = portfolioSummaryService.getPortfolioOverview(token);

        assertNotNull(response);
        assertEquals(200.0, response.getEstimatedReturn());
        assertEquals(100.0, response.getTotalInvestment());
        assertEquals(100.0, response.getTotalProfit());
        verify(auditTrailsService).logsAuditTrails(eq(GeneralConstant.LOG_ACVITIY_DASHBOARD_OVERVIEW), anyString(), anyString(), anyString());
    }

    @Test
    void getPortfolioDetail() throws JsonProcessingException {
        String token = "sample-token";
        Long custId = 1L;
        Long goalId = 10L;

        PortfolioSummary summary = new PortfolioSummary();
        summary.setGoalId(goalId);
        summary.setCustId(custId);
        summary.setEstimatedReturn(300.0);
        summary.setTotalInvestment(150.0);
        summary.setTotalProfit(150.0);
        summary.setProductDetails(new ArrayList<>());

        when(usersClient.getIdCustFromToken(token)).thenReturn(custId);
        when(portfolioSummaryRepository.findByCustIdAndGoalId(custId, goalId)).thenReturn(Optional.of(summary));

        PortfolioSummaryResponse response = portfolioSummaryService.getPortfolioDetail(token, goalId);

        assertNotNull(response);
        assertEquals(custId, response.getCustId());
        assertEquals(goalId, response.getGoalId());
        verify(auditTrailsService).logsAuditTrails(eq(GeneralConstant.LOG_ACVITIY_DASHBOARD_DETAIL), anyString(), anyString(), anyString());
    }
}