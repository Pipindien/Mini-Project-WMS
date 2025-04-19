package com.portfolio_summary_service.app.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "portfolio_product_detail")
@Data
@Builder
public class PortfolioProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPortoDetail;

    private Long goalId;
    private Long custId;
    private Long productId;
    private String productName;
    private Long categoryId;

    private Integer lot;
    private Double buyPrice;
    private Double productRate;
    private Double investmentAmount;
    private Double estimatedReturn;
    private Double profit;

    private Date buyDate;

    @UpdateTimestamp
    private Date lastUpdated;
}
