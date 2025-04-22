package com.transaction.app.dto.portosum;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PortfolioProductDetailResponse {
    private Long custId;
    private Long goalId;
    private Long idPortoDetail;
    private Long productId;
    private String productName;
    private String productCategory;
    private Integer totalLot;
    private Double buyPrice;
    private Double productRate;
    private Double investmentAmount;
    private Double estimatedReturn;
    private Double profit;
    private Date buyDate;
    private Date lastUpdated;
}
