package com.transaction.app.repository;

import com.transaction.app.entity.PortfolioProductDetail;
import com.transaction.app.entity.PortfolioSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioProductDetailRepository extends JpaRepository<PortfolioProductDetail, Long> {
    void deleteByPortfolioSummary(PortfolioSummary summary);

}
