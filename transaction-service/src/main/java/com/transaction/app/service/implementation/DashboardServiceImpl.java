package com.transaction.app.service.implementation;

import com.transaction.app.client.FingolClient;
import com.transaction.app.client.UsersClient;
import com.transaction.app.client.dto.FinancialGoalResponse;
import com.transaction.app.dto.dashboard.DashboardResponse;
import com.transaction.app.dto.dashboard.PortfolioSummaryDashboard;
import com.transaction.app.dto.insight.InsightResponse;
import com.transaction.app.entity.PortfolioSummary;
import com.transaction.app.repository.PortfolioSummaryRepository;
import com.transaction.app.service.DashboardService;
import com.transaction.app.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UsersClient usersClient;

    @Autowired
    private FingolClient fingolClient;

    @Autowired
    private InsightService insightService;

    @Autowired
    private PortfolioSummaryRepository summaryRepository;
    @Override
    public DashboardResponse getDashboard(String token) {
        Long custId = usersClient.getIdCustFromToken(token);

        PortfolioSummary summaryEntity = summaryRepository.findByCustId(custId)
                .orElseThrow(() -> new RuntimeException("Portfolio tidak ditemukan"));

        PortfolioSummaryDashboard summary = mapSummary(summaryEntity);

        List<FinancialGoalResponse> goals = fingolClient.getGoalsByCustId(token);


        return new DashboardResponse(summary, goals);

    }

    private PortfolioSummaryDashboard mapSummary(PortfolioSummary entity) {
        return new PortfolioSummaryDashboard(
                entity.getTotalInvestment(),
                entity.getEstimatedReturn(),
                entity.getTotalProfit()
        );
    }



}
