package com.portfolio_summary_service.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio_summary_service.app.client.FinancialGoalClient;
import com.portfolio_summary_service.app.client.ProductClient;
import com.portfolio_summary_service.app.client.TransactionClient;
import com.portfolio_summary_service.app.client.dto.ProductResponse;
import com.portfolio_summary_service.app.client.dto.TransactionResponse;
import com.portfolio_summary_service.app.dto.PortfolioSummaryResponse;
import com.portfolio_summary_service.app.dto.ProductSummaryResponse;
import com.portfolio_summary_service.app.dto.fingoaldto.UpdateCurrentAmountResponse;
import com.portfolio_summary_service.app.entity.PortfolioProductDetail;
import com.portfolio_summary_service.app.service.PortfolioSummaryService;
import com.portfolio_summary_service.app.utility.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioSummaryServiceImpl implements PortfolioSummaryService {
    @Autowired
    private TransactionClient transactionClient;
    @Autowired
    private ProductClient productClient;
    @Autowired
    private FinancialGoalClient financialGoalClient;

    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public PortfolioSummaryResponse getSummaryByGoalId(Long goalId, Long custId, String token) {
        return buildPortfolioSummary(goalId, custId, token);
    }

    private PortfolioSummaryResponse buildPortfolioSummary(Long goalId, Long custId, String token) {
        List<TransactionResponse> allTransactions = transactionClient.getMyTransactionsByGoal(token, goalId);
        List<TransactionResponse> goalTransactions = new ArrayList<>();

        for (TransactionResponse transaction : allTransactions) {
            if (transaction.getGoalId().equals(goalId)) {
                goalTransactions.add(transaction);
            }
        }

        if (goalTransactions.isEmpty()) {
            return PortfolioSummaryResponse.builder()
                    .goalId(goalId)
                    .custId(custId)
                    .totalInvestment(0.0)
                    .estimatedReturn(0.0)
                    .totalProfit(0.0)
                    .productSummaryResponses(new ArrayList<>())
                    .build();
        }

        List<ProductSummaryResponse> productSummaries = new ArrayList<>();
        Double totalInvestment = 0.0;
        Double totalEstimatedReturn = 0.0;

        for (TransactionResponse transaction : goalTransactions) {
            ProductResponse product = productClient.getProductById(transaction.getProductId());


            Double investmentAmount = transaction.getProductPrice() * transaction.getLot(); //bisa langsung getAmount sebenarnya
            Double investmentAmount1 = transaction.getAmount(); //bisa langsung getAmount sebenarnya
            /*int nMonths = DateHelper.calculateMonthDiff(transaction.getUpdateDate(), LocalDate.now());
            Double multiplier = Math.pow(1 + product.getProductRate(), nMonths);*/

            int nDays = (int) DateHelper.calculateDayDiff(transaction.getUpdateDate(), LocalDate.now());
            Double multiplier = Math.pow(1 + product.getProductRate(), nDays);

            Double estimatedReturn =  (investmentAmount * multiplier);
            Double profit = estimatedReturn - investmentAmount;

            ProductSummaryResponse summary = ProductSummaryResponse.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .categoryId(product.getCategoryId())
                    .totalLot(transaction.getLot())
                    .buyPrice(transaction.getProductPrice())
                    .productRate(product.getProductRate())
                    .investmentAmount(investmentAmount)
                    .estimatedReturn(estimatedReturn)
                    .profit(profit)
                    .build();

            productSummaries.add(summary);
            totalInvestment += investmentAmount;
            totalEstimatedReturn += estimatedReturn;
        }

        Double totalProfit = totalEstimatedReturn - totalInvestment;

        return PortfolioSummaryResponse.builder()
                .goalId(goalId)
                .custId(custId)
                .totalInvestment(totalInvestment)
                .estimatedReturn(totalEstimatedReturn)
                .totalProfit(totalProfit)
                .productSummaryResponses(productSummaries)
                .build();
    }

    @Override
    public List<Map<String, Object>> getAllSummariesByCustomerId(Long custId) {
        return List.of();
    }

    @Override
    public UpdateCurrentAmountResponse recalculate(Long goalId) {
        return null;
    }

}
