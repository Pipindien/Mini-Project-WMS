package com.transaction.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.app.client.FingolClient;
import com.transaction.app.client.ProductClient;
import com.transaction.app.client.UsersClient;
import com.transaction.app.client.dto.ProductResponse;
import com.transaction.app.constant.GeneralConstant;
import com.transaction.app.dto.insight.InsightResponse;
import com.transaction.app.dto.portosum.PortfolioProductDetailResponse;
import com.transaction.app.dto.portosum.PortfolioSummaryResponse;
import com.transaction.app.entity.PortfolioProductDetail;
import com.transaction.app.entity.PortfolioSummary;
import com.transaction.app.entity.Transaction;
import com.transaction.app.repository.PortfolioSummaryRepository;
import com.transaction.app.repository.TransactionRepository;
import com.transaction.app.service.AuditTrailsService;
import com.transaction.app.service.InsightService;
import com.transaction.app.service.PortfolioSummaryService;
import com.transaction.app.utility.DateHelper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
    private InsightService insightService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    @Transactional
    public PortfolioSummaryResponse upsertPortfolioSummary(Long goalId, String token) throws JsonProcessingException {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token must not be null or empty");
        }
        Long custId = usersClient.getIdCustFromToken(token);
        if (custId == null) {
            throw new RuntimeException("Invalid token: custId could not be resolved");
        }
        List<Transaction> transactions = transactionRepository.findByCustIdAndGoalIdAndStatus(custId, goalId, "SUCCESS");
        if (transactions.isEmpty()) {
            throw new NoSuchElementException("No successful transactions found for this goal");
        }

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

        List<PortfolioProductDetail> detailList = new ArrayList<>();
        double totalInvestment = 0.0;
        double totalEstimatedReturn = 0.0;

        for (Transaction transaction : transactions) {
            if (transaction.getLot() <= 0) continue;

            ProductResponse product = productClient.getProductById(transaction.getProductId());
            if (product == null) {
                log.warn("Product with ID {} not found, skipping transaction ID {}", transaction.getProductId(), transaction.getId());
                continue;
            }

            if (product.getProductRate() < 0) {
                log.warn("Negative product rate for product ID {}, skipping", product.getProductId());
                continue;
            }

            double productPrice = product.getProductPrice();
            double investmentAmount = productPrice * transaction.getLot();
            int nDays = (int) DateHelper.calculateDayDiff(transaction.getCreatedDate(), LocalDate.now());

            double dailyRate = Math.pow(1 + product.getProductRate(), 1.0 / 30) - 1;
            double multiplier = Math.pow(1 + dailyRate, nDays);

            double estimatedReturn = investmentAmount * multiplier;
            double profit = estimatedReturn - investmentAmount;

            detailList.add(PortfolioProductDetail.builder()
                    .goalId(goalId)
                    .custId(custId)
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productCategory(product.getProductCategory())
                    .lot(transaction.getLot())
                    .buyPrice(productPrice)
                    .productRate(product.getProductRate())
                    .investmentAmount(investmentAmount)
                    .estimatedReturn(estimatedReturn)
                    .profit(profit)
                    .buyDate(transaction.getCreatedDate())
                    .lastUpdated(new Date())
                    .portfolioSummary(summary)
                    .build());

            totalInvestment += investmentAmount;
            totalEstimatedReturn += estimatedReturn;
        }

        if (detailList.isEmpty()) {
            log.warn("No portfolio details generated for custId: {}, goalId: {}", custId, goalId);
        }

        summary.setTotalInvestment(totalInvestment);
        summary.setEstimatedReturn(totalEstimatedReturn);
        summary.setTotalProfit(totalEstimatedReturn - totalInvestment);
        summary.getProductDetails().addAll(detailList);
        summary = portfolioSummaryRepository.save(summary);

        if (summary == null || summary.getPortoId() == null) {
            throw new RuntimeException("Failed to save portfolio summary");
        }

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_UPSERTPORTFOLIO,
                mapper.writeValueAsString(custId),
                mapper.writeValueAsString(custId),
                "Update and Insert Portfolio Summary"
        );

        log.info("Successfully upserted portfolio for custId: {}, goalId: {}, totalInvestment: {}, estimatedReturn: {}",
                custId, goalId, totalInvestment, totalEstimatedReturn);

        return PortfolioSummaryResponse.builder()
                .portoId(summary.getPortoId())
                .goalId(goalId)
                .custId(custId)
                .totalInvestment(totalInvestment)
                .estimatedReturn(totalEstimatedReturn)
                .totalProfit(summary.getTotalProfit())
                .lastUpdated(new Date())
                .portfolioProductDetails(detailList.stream().map(detail -> PortfolioProductDetailResponse.builder()
                        .productId(detail.getProductId())
                        .productName(detail.getProductName())
                        .productName(detail.getProductName())
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
    public void updateProgress(Long goalId, String token) throws JsonProcessingException {
        PortfolioSummary summary = portfolioSummaryRepository.findOneByGoalId(goalId)
                .orElseThrow(() -> new RuntimeException("Goal ID not found in portfolio summary: " + goalId));

        double currentAmount = summary.getEstimatedReturn();
        InsightResponse insight = insightService.generateInsight(goalId, token);
        String updateResult = fingolClient.updateCurrentAmountAndInsight(goalId, currentAmount, insight.getInsightMessage(), token);
        System.out.println("Update progress result: " + updateResult);

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_UPDATE_PROGRESS,
                mapper.writeValueAsString(goalId),
                mapper.writeValueAsString(goalId),
                "Update Progress in Financial Goal"
        );

    }

    @Override
    public PortfolioSummaryResponse getPortfolioOverview(String token) throws JsonProcessingException {
        Long custId = usersClient.getIdCustFromToken(token);
        List<PortfolioSummary> summaries = portfolioSummaryRepository.findAllByCustId(custId);

        if (summaries.isEmpty()) {
            return PortfolioSummaryResponse.builder()
                    .custId(custId)
                    .totalInvestment(0.0)
                    .estimatedReturn(0.0)
                    .totalProfit(0.0)
                    .returnPercentage(0.0)
                    .categoryAllocation(new HashMap<>())
                    .build();
        }

        double totalInvestment = 0.0;
        double totalEstimatedReturn = 0.0;
        double totalProfit = 0.0;
        Map<String, Double> categoryDistribution = new HashMap<>();

        for (PortfolioSummary summary : summaries) {
            totalInvestment += summary.getTotalInvestment();
            totalEstimatedReturn += summary.getEstimatedReturn();
            totalProfit += summary.getTotalProfit();

            for (PortfolioProductDetail detail : summary.getProductDetails()) {
                categoryDistribution.merge(
                        detail.getProductCategory(),
                        detail.getInvestmentAmount() != null ? detail.getInvestmentAmount() : 0.0,
                        Double::sum
                );
            }
        }

        // Convert to percentage
        Map<String, Double> categoryPercentage = new HashMap<>();
        for (Map.Entry<String, Double> entry : categoryDistribution.entrySet()) {
            categoryPercentage.put(entry.getKey(), (entry.getValue() / totalInvestment) * 100);
        }

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_DASHBOARD_OVERVIEW,
                mapper.writeValueAsString(custId),
                mapper.writeValueAsString(custId),
                "Get Dashboard Overview"
        );

        return PortfolioSummaryResponse.builder()
                .custId(custId)
                .totalInvestment(totalInvestment)
                .estimatedReturn(totalEstimatedReturn)
                .totalProfit(totalProfit)
                .returnPercentage(totalInvestment != 0 ? (totalProfit / totalInvestment) * 100 : 0.0)
                .categoryAllocation(categoryPercentage)
                .build();
    }


    @Override
    public PortfolioSummaryResponse getPortfolioDetail(String token, Long goalId) throws JsonProcessingException {
        Long custId = usersClient.getIdCustFromToken(token);
        Optional<PortfolioSummary> optional = portfolioSummaryRepository.findByCustIdAndGoalId(custId, goalId);

        if (optional.isEmpty()) {
            throw new RuntimeException("Portfolio summary not found for Cust Id: " + custId + " and goal ID " + goalId);
        }

        PortfolioSummary summary = optional.get();

        List<PortfolioProductDetailResponse> detailResponses = summary.getProductDetails().stream()
                .filter(detail -> detail.getLot() != null && detail.getLot() > 0)
                .map(detail -> PortfolioProductDetailResponse.builder()
                        .productId(detail.getProductId())
                        .productName(detail.getProductName())
                        .productRate(detail.getProductRate())
                        .productCategory(detail.getProductCategory())
                        .totalLot(detail.getLot())
                        .buyPrice(detail.getBuyPrice())
                        .investmentAmount(detail.getInvestmentAmount())
                        .estimatedReturn(detail.getEstimatedReturn())
                        .profit(detail.getProfit())
                        .buyDate(detail.getBuyDate())
                        .build())
                .collect(Collectors.toList());

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_DASHBOARD_DETAIL,
                mapper.writeValueAsString(custId),
                mapper.writeValueAsString(custId),
                "Get Detail Dashboard"
        );

        return PortfolioSummaryResponse.builder()
                .custId(summary.getCustId())
                .goalId(summary.getGoalId())
                .totalInvestment(summary.getTotalInvestment())
                .estimatedReturn(summary.getEstimatedReturn())
                .totalProfit(summary.getTotalProfit())
                .returnPercentage(summary.getTotalInvestment() != 0 ? (summary.getTotalProfit() / summary.getTotalInvestment()) * 100 : 0.0)
                .portfolioProductDetails(detailResponses)
                .build();
    }
}