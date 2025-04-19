package com.portfolio_summary_service.app.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "portfolio_summary")
@Data
@Builder
public class PortfolioSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portoId;

    private Long goalId;
    private Long custId;

    private Double totalInvestment;  // total modal yang sudah diinvestasikan
    private Double estimatedReturn;  // estimasi total pengembalian berdasarkan rate
    private Double totalProfit;      // estimatedReturn - totalInvestment

    @UpdateTimestamp
    private LocalDate lastUpdated;
}
