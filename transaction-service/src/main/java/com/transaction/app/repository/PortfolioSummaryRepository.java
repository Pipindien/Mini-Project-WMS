package com.transaction.app.repository;

import com.transaction.app.entity.PortfolioSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioSummaryRepository extends JpaRepository<PortfolioSummary, Long> {

    @Query("SELECT ps FROM PortfolioSummary ps WHERE ps.goalId = :goalId")
    Optional<PortfolioSummary> findOneByGoalId(@Param("goalId") Long goalId);


    Optional<PortfolioSummary> findByCustIdAndGoalId(Long custId, Long goalId);
    List<PortfolioSummary> findAllByCustId(Long custId);

}
