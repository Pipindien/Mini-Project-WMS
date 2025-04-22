package com.transaction.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolio_summary")
@NoArgsConstructor
@AllArgsConstructor
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
    @OneToMany(mappedBy = "portfolioSummary", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<PortfolioProductDetail> productDetails = new ArrayList<>();
}
