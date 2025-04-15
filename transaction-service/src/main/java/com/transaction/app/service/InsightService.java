package com.transaction.app.service;

import com.transaction.app.dto.insight.InsightResponse;

public interface InsightService {
    InsightResponse generateInsight(Long goalId, String token);
}
