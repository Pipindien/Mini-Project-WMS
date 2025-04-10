package com.financial_goal_service.app.service.impl;

import com.financial_goal_service.app.entity.FinancialGoal;
import com.financial_goal_service.app.service.InsightService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class InsightServiceImpl implements InsightService {

    @Override
    public String generateInsight(FinancialGoal goal) {
        double annualReturn = getAnnualReturn(goal.getRiskTolerance());
        double futureValue = calculateFutureValue(goal);
        double monthlyInvestmentNeeded = calculateMonthlyInvestment(goal, annualReturn);

        String insightMessage = generateMessage(goal, futureValue, monthlyInvestmentNeeded);
        return insightMessage;
    }

    private double getAnnualReturn(String riskTolerance) {
        return switch (riskTolerance.toLowerCase()) {
            case "conservative" -> 0.04;
            case "moderate" -> 0.07;
            case "aggressive" -> 0.10;
            default -> 0.05;
        };
    }

    private long getMonthsRemaining(FinancialGoal goal) {
        LocalDate now = LocalDate.now();
        LocalDate target = goal.getTargetDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.MONTHS.between(now, target);
    }

    private double calculateFutureValue(FinancialGoal goal) {
        double monthlyReturn = getAnnualReturn(goal.getRiskTolerance()) / 12;
        long months = getMonthsRemaining(goal);
        return goal.getCurrentAmount() * Math.pow(1 + monthlyReturn, months);
    }

    private double calculateMonthlyInvestment(FinancialGoal goal, double annualReturn) {
        long months = getMonthsRemaining(goal);
        double monthlyReturn = annualReturn / 12;
        double fv = goal.getTargetAmount();
        double currentFV = calculateFutureValue(goal);

        double needed = fv - currentFV;

        if (needed <= 0 || months <= 0) return 0;

        return (needed * monthlyReturn) / (Math.pow(1 + monthlyReturn, months) - 1);
    }

    private String generateMessage(FinancialGoal goal, double futureValue, double monthlyInvestment) {
        double percentageSaved = (double) goal.getCurrentAmount() / goal.getTargetAmount();
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(),
                goal.getTargetDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        if (goal.getCurrentAmount() == 0) {
            return "💡 Kamu sudah punya tujuan yang jelas! Yuk mulai investasi pertamamu untuk mencapainya.";
        } else if (percentageSaved >= 1.0) {
            return "🎉 Goal tercapai! Selamat ya! 🎉";
        } else if (daysRemaining <= 0) {
            return "⏰ Waktunya sudah lewat nih. Yuk evaluasi dan update targetmu.";
        } else if (percentageSaved >= 0.7) {
            return "🚀 Sudah hampir sampai! Terus semangat nabungnya!";
        } else if (percentageSaved < 0.2) {
            return "📉 Masih jauh dari target. Yuk tingkatkan jumlah investasimu!";
        } else if (monthlyInvestment <= 0) {
            return "✅ Kamu sudah di jalur yang tepat, terus lanjutkan ya!";
        } else {
            return String.format(
                    "📊 Untuk mencapai tujuanmu, kamu perlu investasi sekitar Rp%.0f per bulan. Dengan dana yang sekarang, diperkirakan bisa tumbuh jadi Rp%.0f sampai %s.",
                    monthlyInvestment,
                    futureValue,
                    goal.getTargetDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()
            );
        }
    }

}
