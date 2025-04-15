package com.transaction.app.service;

import com.transaction.app.dto.dashboard.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboard(String token);
}
