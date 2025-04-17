package com.transaction.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.client.FingolClient;
import com.transaction.app.client.ProductClient;
import com.transaction.app.client.UsersClient;
import com.transaction.app.client.dto.ProductResponse;
import com.transaction.app.dto.insight.InsightResponse;
import com.transaction.app.dto.portosum.PortfolioProductDetailResponse;
import com.transaction.app.dto.portosum.PortfolioSummaryResponse;
import com.transaction.app.entity.PortfolioProductDetail;
import com.transaction.app.entity.PortfolioSummary;
import com.transaction.app.entity.Transaction;
import com.transaction.app.repository.PortfolioProductDetailRepository;
import com.transaction.app.repository.PortfolioSummaryRepository;
import com.transaction.app.repository.TransactionRepository;
import com.transaction.app.service.AuditTrailsService;
import com.transaction.app.service.InsightService;
import com.transaction.app.service.PortfolioSummaryService;
import com.transaction.app.utility.DateHelper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioSummaryServiceImpl implements PortfolioSummaryService {
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
    private PortfolioSummaryRepository portfolioSummaryRepository;

    @Autowired
    private PortfolioProductDetailRepository portfolioProductDetailRepository;

    @Autowired
    private InsightService insightService;

    @Override
    @Transactional
    public PortfolioSummaryResponse upsertPortfolioSummary(Long goalId, String token) throws JsonProcessingException {
        Long custId = usersClient.getIdCustFromToken(token);
        List<Transaction> transactions = transactionRepository.findByCustIdAndGoalIdAndStatus(custId, goalId, "SUCCESS");
        if (transactions.isEmpty()) return null;

        PortfolioSummary summary = portfolioSummaryRepository.findByCustIdAndGoalId(custId, goalId)
                .orElseGet(() -> portfolioSummaryRepository.save(PortfolioSummary.builder()
                        .goalId(goalId)
                        .custId(custId)
                        .build()));

        if (summary.getProductDetails() != null) {
            summary.getProductDetails().clear();
        } else {
            summary.setProductDetails(new ArrayList<>());
        }

        summary.getProductDetails().clear();

        List<PortfolioProductDetail> detailList = new ArrayList<>();
        double totalInvestment = 0.0;
        double totalEstimatedReturn = 0.0;

        for (Transaction transaction : transactions) {
            ProductResponse product = productClient.getProductById(transaction.getProductId());
            double investmentAmount = transaction.getProductPrice() * transaction.getLot();
            int nMonth = (int) DateHelper.calculateMonthDiff(transaction.getCreatedDate(), LocalDate.now());
            double multiplier = Math.pow(1 + product.getProductRate(), nMonth);
            double estimatedReturn = investmentAmount * multiplier;
            double profit = estimatedReturn - investmentAmount;

            detailList.add(PortfolioProductDetail.builder()
                    .goalId(goalId)
                    .custId(custId)
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .categoryId(product.getCategoryId())
                    .lot(transaction.getLot())
                    .buyPrice(transaction.getProductPrice())
                    .productRate(product.getProductRate())
                    .investmentAmount(investmentAmount)
                    .estimatedReturn(estimatedReturn)
                    .profit(profit)
                    .buyDate(transaction.getCreatedDate())
                    .portfolioSummary(summary)
                    .build());

            totalInvestment += investmentAmount;
            totalEstimatedReturn += estimatedReturn;
        }

        summary.setTotalInvestment(totalInvestment);
        summary.setEstimatedReturn(totalEstimatedReturn);
        summary.setTotalProfit(totalEstimatedReturn - totalInvestment);
        summary.getProductDetails().addAll(detailList);

        summary = portfolioSummaryRepository.save(summary);

        return PortfolioSummaryResponse.builder()
                .portoId(summary.getPortoId())
                .goalId(goalId)
                .custId(custId)
                .totalInvestment(totalInvestment)
                .estimatedReturn(totalEstimatedReturn)
                .totalProfit(summary.getTotalProfit())
                .portfolioProductDetails(detailList.stream().map(detail -> PortfolioProductDetailResponse.builder()
                        .productId(detail.getProductId())
                        .productName(detail.getProductName())
                        .categoryId(detail.getCategoryId())
                        .totalLot(detail.getLot())
                        .buyPrice(detail.getBuyPrice())
                        .productRate(detail.getProductRate())
                        .investmentAmount(detail.getInvestmentAmount())
                        .estimatedReturn(detail.getEstimatedReturn())
                        .profit(detail.getProfit())
                        .build()).toList())
                .build();
    }


    @Override
    public void updateProgress(Long goalId, String token) {
        PortfolioSummary summary = portfolioSummaryRepository.findOneByGoalId(goalId)
                .orElseThrow(() -> new RuntimeException("Goal ID not found in portfolio summary: " + goalId));

        double currentAmount = summary.getTotalInvestment();
        InsightResponse insight = insightService.generateInsight(goalId, token);
        String updateResult = fingolClient.updateCurrentAmountAndInsight(goalId, currentAmount, insight.getInsightMessage(), token);
        System.out.println("Update progress result: " + updateResult);
    }


}