package com.transaction.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.client.FingolClient;
import com.transaction.app.client.ProductClient;
import com.transaction.app.client.dto.FinancialGoalResponse;
import com.transaction.app.client.dto.ProductResponse;
import com.transaction.app.dto.insight.InsightResponse;
import com.transaction.app.entity.PortfolioProductDetail;
import com.transaction.app.entity.PortfolioSummary;
import com.transaction.app.repository.PortfolioSummaryRepository;
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

class InsightServiceImplTest {

    @Mock
    private FingolClient fingolClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private PortfolioSummaryRepository portfolioSummaryRepository;

    @InjectMocks
    private InsightServiceImpl insightService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateInsight() throws JsonProcessingException {
        Long goalId = 1L;
        String token = "token";
        FinancialGoalResponse financialGoal = new FinancialGoalResponse();
        financialGoal.setTargetAmount(10_000_000.0);
        financialGoal.setTargetDate(Date.from(LocalDate.now().plusMonths(12).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        PortfolioProductDetail productDetail = new PortfolioProductDetail();
        productDetail.setInvestmentAmount(5_000_000.0);
        productDetail.setProductRate(0.01); // 1% per bulan

        PortfolioSummary summary = new PortfolioSummary();
        summary.setEstimatedReturn(6_000_000.0);
        summary.setProductDetails(Collections.singletonList(productDetail));

        when(fingolClient.getFinancialGoalById(goalId, token)).thenReturn(financialGoal);
        when(portfolioSummaryRepository.findOneByGoalId(goalId)).thenReturn(Optional.of(summary));

        InsightResponse response = insightService.generateInsight(goalId, token);

        assertNotNull(response);
        assertEquals(goalId, response.getGoalId());
        assertTrue(response.getInsightMessage().contains("per bulan"));
        assertTrue(response.getFutureValue() > 0);
    }

    @Test
    void simulateGoalAchievement() throws JsonProcessingException {
        Long goalId = 1L;
        double monthlyInvestment = 500_000.0;
        String token = "token";

        FinancialGoalResponse financialGoal = new FinancialGoalResponse();
        financialGoal.setTargetAmount(15_000_000.0);

        PortfolioProductDetail productDetail = new PortfolioProductDetail();
        productDetail.setInvestmentAmount(5_000_000.0);
        productDetail.setProductRate(0.01); // 1% per bulan

        PortfolioSummary summary = new PortfolioSummary();
        summary.setEstimatedReturn(6_000_000.0);
        summary.setProductDetails(Collections.singletonList(productDetail));

        when(fingolClient.getFinancialGoalById(goalId, token)).thenReturn(financialGoal);
        when(portfolioSummaryRepository.findOneByGoalId(goalId)).thenReturn(Optional.of(summary));

        InsightResponse response = insightService.simulateGoalAchievement(goalId, monthlyInvestment, token);

        assertNotNull(response);
        assertTrue(response.getInsightMessage().contains("investasi"));
        assertTrue(response.getMonthsToAchieve() > 0);
    }

    @Test
    void simulateProductInvestment() throws JsonProcessingException {
        Long productId = 1L;
        double monthlyInvestment = 1_000_000.0;
        int years = 3;

        ProductResponse product = new ProductResponse();
        product.setProductRate(0.01); // 1% per bulan

        when(productClient.getProductById(productId)).thenReturn(product);

        InsightResponse response = insightService.simulateProductInvestment(productId, monthlyInvestment, years);

        assertNotNull(response);
        assertTrue(response.getInsightMessage().contains("investasi"));
        assertEquals(36, response.getMonthsToAchieve());
        assertTrue(response.getFutureValue() > 0);
    }
}
