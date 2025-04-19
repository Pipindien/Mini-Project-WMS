package com.portfolio_summary_service.app.dto.fingoaldto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCurrentAmountRequest {
    private Long currentAmount;
}
