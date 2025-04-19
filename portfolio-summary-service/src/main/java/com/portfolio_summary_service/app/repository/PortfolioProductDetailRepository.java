package com.portfolio_summary_service.app.repository;

import com.portfolio_summary_service.app.entity.PortfolioProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioProductDetailRepository extends JpaRepository<PortfolioProductDetail, Long> {
    List<PortfolioProductDetail> findByGoalIdAndCustId(Long goalId, Long custId);

    List<PortfolioProductDetail> findByCustId(Long custId);

}