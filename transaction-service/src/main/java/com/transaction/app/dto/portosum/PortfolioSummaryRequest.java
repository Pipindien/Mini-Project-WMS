package com.transaction.app.dto.portosum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortfolioSummaryRequest {
    private Long goalId;
    private Long custId;
}
