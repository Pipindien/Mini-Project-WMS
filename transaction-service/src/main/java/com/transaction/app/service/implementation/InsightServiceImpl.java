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
        FinancialGoalResponse request = new FinancialGoalResponse();
        request.setGoalId(goalId);
        FinancialGoalResponse financialGoalResponse = fingolClient.getFinancialGoalById(request, token);

        if (financialGoalResponse == null) {
            throw new RuntimeException("Financial Goal tidak ditemukan dengan ID: " + goalId);
        }

        // Ambil data dari Fingol microservice
        Double targetAmount = financialGoalResponse.getTargetAmount();
        Date targetDateUtil = financialGoalResponse.getTargetDate();
        LocalDate targetDate = convertToLocalDate(targetDateUtil); // Convert Date to LocalDate

        // Ambil data dari Portfolio Summary (microservice ini sendiri)
        PortfolioSummary summary = portfolioSummaryRepository.findOneByGoalId(goalId)
                .orElseThrow(() -> new RuntimeException("Goal ID not found in portfolio summary: " + goalId));

        double currentAmount = summary.getTotalInvestment();
        List<PortfolioProductDetail> products = summary.getProductDetails();
        double avgDailyRate = calculateAverageDailyRate(products);

        long monthsRemaining = ChronoUnit.MONTHS.between(LocalDate.now(), targetDate);
        double monthlyRate = avgDailyRate; // INI RATE NYA DIITUNG PER MONTH DULU

        double futureValue = currentAmount * Math.pow(1 + monthlyRate, monthsRemaining);
        double needed = targetAmount - futureValue;

        double monthlyInvestment = (needed <= 0 || monthsRemaining <= 0) ? 0 :
                (needed * monthlyRate) / (Math.pow(1 + monthlyRate, monthsRemaining) - 1);

        String message = generateMessage(currentAmount, targetAmount, futureValue, monthlyInvestment, targetDate);

        return new InsightResponse(goalId, message, futureValue, monthlyInvestment);
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

    private String generateMessage(double currentAmount, double targetAmount, double futureValue, double monthlyInvestment, LocalDate targetDate) {
        double percentageSaved = currentAmount / targetAmount;
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);

        if (currentAmount == 0) {
            return "💡 Yuk mulai investasi pertamamu untuk capai tujuanmu!";
        } else if (percentageSaved >= 1.0) {
            return "🎉 Goal tercapai! Selamat ya!";
        } else if (daysRemaining <= 0) {
            return "⏰ Waktunya sudah lewat nih. Yuk evaluasi dan update targetmu.";
        } else if (percentageSaved >= 0.7) {
            return "🚀 Sudah hampir sampai! Terus semangat nabungnya!";
        } else if (percentageSaved < 0.2) {
            return "📉 Masih jauh dari target. Yuk tingkatkan jumlah investasimu!";
        } else if (monthlyInvestment <= 0) {
            return "✅ Kamu sudah di jalur yang tepat, lanjutkan terus ya!";
        } else {
            return String.format(
                    "📊 Untuk capai tujuanmu, perlu investasi ~Rp%.0f per bulan. Dengan saldo sekarang, bisa tumbuh jadi Rp%.0f sampai %s.",
                    monthlyInvestment, futureValue, targetDate
            );
        }
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
