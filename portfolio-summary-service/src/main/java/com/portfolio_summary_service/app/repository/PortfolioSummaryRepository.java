package com.portfolio_summary_service.app.repository;

import com.portfolio_summary_service.app.entity.PortfolioSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioSummaryRepository extends JpaRepository<PortfolioSummary, Long> {
    List<PortfolioSummary> findByCustId(Long custId);

    List<PortfolioSummary> findByGoalId(Long goalId);

    List<PortfolioSummary> findByCustIdAndGoalId(Long custId, Long goalId);
}
