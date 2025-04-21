package com.transaction.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.app.client.FingolClient;
import com.transaction.app.client.ProductClient;
import com.transaction.app.client.dto.FinancialGoalResponse;
import com.transaction.app.client.dto.ProductResponse;
import com.transaction.app.constant.GeneralConstant;
import com.transaction.app.dto.insight.InsightResponse;
import com.transaction.app.entity.PortfolioProductDetail;
import com.transaction.app.entity.PortfolioSummary;
import com.transaction.app.repository.PortfolioSummaryRepository;
import com.transaction.app.service.AuditTrailsService;
import com.transaction.app.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InsightServiceImpl implements InsightService {
    @Autowired
    private FingolClient fingolClient;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private PortfolioSummaryRepository portfolioSummaryRepository;

    @Autowired
    private AuditTrailsService auditTrailsService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public InsightResponse generateInsight(Long goalId, String token) throws JsonProcessingException {
        FinancialGoalResponse financialGoalResponse = fingolClient.getFinancialGoalById(goalId, token);

        if (financialGoalResponse == null) {
            throw new RuntimeException("Financial Goal tidak ditemukan dengan ID: " + goalId);
        }

        Double targetAmount = financialGoalResponse.getTargetAmount();
        Date targetDateUtil = financialGoalResponse.getTargetDate();
        LocalDate targetDate = convertToLocalDate(targetDateUtil); // Convert Date to LocalDate

        PortfolioSummary summary = portfolioSummaryRepository.findOneByGoalId(goalId)
                .orElseThrow(() -> new RuntimeException("Goal ID not found in portfolio summary: " + goalId));

        double currentAmount = summary.getEstimatedReturn();
        List<PortfolioProductDetail> products = summary.getProductDetails();
        double avgDailyRate = calculateAverageMonthlyRate(products);

        long monthsRemaining = ChronoUnit.MONTHS.between(LocalDate.now(), targetDate);
        double monthlyRate = avgDailyRate;

        double futureValue = currentAmount * Math.pow(1 + monthlyRate, monthsRemaining);

        double needed = targetAmount - futureValue;

        double monthlyInvestment = (needed <= 0 || monthsRemaining <= 0) ? 0 :
                (needed * monthlyRate) / (Math.pow(1 + monthlyRate, monthsRemaining) - 1);

        long monthsToAchieve = 0;
        if (currentAmount > 0 && monthlyRate > 0 && targetAmount > currentAmount) {
            monthsToAchieve = (long) (Math.log(targetAmount / currentAmount) / Math.log(1 + monthlyRate));
        }


        String message = generateMessage(currentAmount, targetAmount, futureValue, monthlyInvestment, targetDate, monthsToAchieve);

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACTIVITY_GENERATE_INSIGHT_MESSAGE,
                mapper.writeValueAsString(message),
                mapper.writeValueAsString(message),
                "Generate Product Simulation"
        );

        return new InsightResponse(goalId, message, futureValue, monthlyInvestment, (int) monthsToAchieve);
    }

    public InsightResponse simulateGoalAchievement(Long goalId, double monthlyInvestment, String token) throws JsonProcessingException {
        FinancialGoalResponse financialGoalResponse = fingolClient.getFinancialGoalById(goalId, token);

        if (financialGoalResponse == null) {
            throw new RuntimeException("Financial Goal tidak ditemukan dengan ID: " + goalId);
        }

        double targetAmount = financialGoalResponse.getTargetAmount();

        PortfolioSummary summary = portfolioSummaryRepository.findOneByGoalId(goalId)
                .orElseThrow(() -> new RuntimeException("Goal ID not found in portfolio summary: " + goalId));

        double currentAmount = summary.getEstimatedReturn(); // sudah termasuk profit
        List<PortfolioProductDetail> products = summary.getProductDetails();
        double monthlyRate = calculateAverageMonthlyRate(products); // dalam bentuk bulanan

        double total = currentAmount;
        long months = 0;

        // Loop untuk estimasi kapan target tercapai
        while (total < targetAmount && months < 1000) {
            total = total * (1 + monthlyRate);
            total += monthlyInvestment;
            months++;
        }

        // Estimasi saldo setelah 1 tahun
        double totalOneYear = currentAmount;
        for (int i = 0; i < 12; i++) {
            totalOneYear = totalOneYear * (1 + monthlyRate);
            totalOneYear += monthlyInvestment;
        }

        String additionalNote = String.format(
                "📈 Dalam 1 tahun, estimasi saldo kamu bisa tumbuh jadi sekitar Rp%,.0f.", totalOneYear
        );

        String message;
        if (total >= targetAmount) {
            message = String.format(
                    "✅ Dengan investasi Rp%.0f per bulan, kamu bisa capai tujuanmu dalam %d bulan! %s",
                    monthlyInvestment, months, additionalNote
            );
        } else {
            message = "❌ Dengan investasi bulanan tersebut, kamu belum bisa capai target dalam waktu wajar.";
        }

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACTIVITY_GENERATE_GOAL_SIMULATION,
                mapper.writeValueAsString(message),
                mapper.writeValueAsString(message),
                "Generate Product Simulation"
        );

        return new InsightResponse(goalId, message, 0.0, monthlyInvestment, (int) months);
    }

    @Override
    public InsightResponse simulateProductInvestment(Long productId, double monthlyInvestment, int years) throws JsonProcessingException {
        if (years < 1 || years > 10) {
            throw new IllegalArgumentException("Durasi harus antara 1 hingga 10 tahun");
        }

        ProductResponse product = productClient.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Produk tidak ditemukan dengan ID: " + productId);
        }

        double monthlyRate = product.getProductRate(); // Sudah asumsi rate bulanan
        int months = years * 12;
        double total = 0;

        // Loop dengan bunga dihitung dulu, lalu ditambah investasi
        for (int i = 0; i < months; i++) {
            total = total * (1 + monthlyRate);
            total += monthlyInvestment;
        }

        String message = String.format(
                "💰 Jika kamu investasi Rp%.0f/bulan ke produk ini selama %d tahun, totalmu bisa tumbuh jadi sekitar Rp%.0f (termasuk return).",
                monthlyInvestment, years, total
        );
        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACTIVITY_GENERATE_PRODUCT_SIMULATION,
                mapper.writeValueAsString(message),
                mapper.writeValueAsString(message),
                "Generate Product Simulation"
        );

        return new InsightResponse(null, message, total, monthlyInvestment, months);
    }


    private double calculateAverageMonthlyRate(List<PortfolioProductDetail> productDetails) {
        if (productDetails == null || productDetails.isEmpty()) {
            return 0;
        }

        double totalAmount = 0;
        double weightedSum = 0;

        for (PortfolioProductDetail detail : productDetails) {
            totalAmount += detail.getInvestmentAmount();
            weightedSum += detail.getInvestmentAmount() * detail.getProductRate();
        }

        return totalAmount == 0 ? 0 : weightedSum / totalAmount;
    }

    private String generateMessage(double currentAmount, double targetAmount, double futureValue, double monthlyInvestment, LocalDate targetDate, long monthsToAchieve) {
        double percentageSaved = currentAmount / targetAmount;
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);

        if (currentAmount == 0) {
            return "💡 Yuk mulai investasi pertamamu untuk capai tujuanmu!";
        } else {
            return String.format(
                    "📊 Untuk capai tujuanmu, perlu investasi ~Rp%.0f per bulan. Dengan saldo sekarang, bisa tumbuh jadi Rp%.0f sampai %s.\n" +
                    "Dengan saldo sekarang dan return saat ini, kamu bisa capai goal ini dalam sekitar %d bulan!",
                    monthlyInvestment, futureValue, targetDate, monthsToAchieve
            );
        }
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
