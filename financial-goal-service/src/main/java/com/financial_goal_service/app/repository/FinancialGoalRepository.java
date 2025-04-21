package com.financial_goal_service.app.repository;

import com.financial_goal_service.app.entity.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {

    @Query("SELECT fg FROM FinancialGoal fg WHERE fg.goalId = :goalId AND fg.deleted = false")
    Optional<FinancialGoal> findByGoalId(@Param("goalId") Long goalId);

    @Query("SELECT fg FROM FinancialGoal fg WHERE fg.goalName = :goalName AND fg.deleted = false")
    Optional<FinancialGoal> findByGoalName(@Param("goalName") String goalName);

    @Query("SELECT fg FROM FinancialGoal fg WHERE fg.deleted = false AND fg.custId = :custId " +
            "AND (:status IS NULL OR fg.status = :status)")
    List<FinancialGoal> findByCustIdAndStatus(
            @Param("custId") Long custId,
            @Param("status") String status
    );

    @Modifying
    @Transactional
    @Query("UPDATE FinancialGoal fg SET fg.currentAmount = :amount WHERE fg.goalId = :goalId")
    void updateCurrentAmount(@Param("goalId") Long goalId, @Param("amount") Double amount);

}


