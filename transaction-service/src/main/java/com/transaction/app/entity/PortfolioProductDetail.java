package com.transaction.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "portfolio_product_detail")
@NoArgsConstructor
@AllArgsConstructor
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
    private String productCategory;

    private Integer lot;
    private Double buyPrice;
    private Double productRate;
    private Double investmentAmount;
    private Double estimatedReturn;
    private Double profit;

    private Date buyDate;

    @UpdateTimestamp
    private Date lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "porto_id")
    private PortfolioSummary portfolioSummary;
}
