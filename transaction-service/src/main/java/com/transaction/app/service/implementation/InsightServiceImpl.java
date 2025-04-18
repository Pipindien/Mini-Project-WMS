package com.transaction.app.service.implementation;

import com.transaction.app.client.FingolClient;
import com.transaction.app.client.dto.FinancialGoalResponse;
import com.transaction.app.dto.insight.InsightResponse;
import com.transaction.app.entity.PortfolioProductDetail;
import com.transaction.app.entity.PortfolioSummary;
import com.transaction.app.repository.PortfolioSummaryRepository;
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
    private PortfolioSummaryRepository portfolioSummaryRepository;

    @Override
    public InsightResponse generateInsight(Long goalId, String token) {
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
        double avgDailyRate = calculateAverageDailyRate(products);

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

        return new InsightResponse(goalId, message, futureValue, monthlyInvestment, monthsToAchieve);
    }

    public InsightResponse simulateGoalAchievement(Long goalId, double monthlyInvestment, String token) {
        FinancialGoalResponse financialGoalResponse = fingolClient.getFinancialGoalById(goalId, token);

        if (financialGoalResponse == null) {
            throw new RuntimeException("Financial Goal tidak ditemukan dengan ID: " + goalId);
        }

        double targetAmount = financialGoalResponse.getTargetAmount();

        PortfolioSummary summary = portfolioSummaryRepository.findOneByGoalId(goalId)
                .orElseThrow(() -> new RuntimeException("Goal ID not found in portfolio summary: " + goalId));

        double currentAmount = summary.getEstimatedReturn(); // sudah termasuk profit
        List<PortfolioProductDetail> products = summary.getProductDetails();
        double monthlyRate = calculateAverageDailyRate(products); // sudah dalam bentuk bulanan

        double total = currentAmount;
        long months = 0;

        while (total < targetAmount && months < 1000) {
            total = (total + monthlyInvestment) * (1 + monthlyRate);
            months++;
        }

        String message;
        if (total >= targetAmount) {
            message = String.format(
                    "✅ Dengan investasi Rp%.0f per bulan, kamu bisa capai tujuanmu dalam %d bulan!",
                    monthlyInvestment, months
            );
        } else {
            message = "❌ Dengan investasi bulanan tersebut, kamu belum bisa capai target dalam waktu wajar.";
        }

        return new InsightResponse(goalId, message, total, monthlyInvestment, months);
    }


    private double calculateAverageDailyRate(List<PortfolioProductDetail> productDetails) {
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
