package com.transaction.app.repository;

import com.transaction.app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("Select t From Transaction t where t.trxNumber=:trxNumber")
    Transaction findByTrxNumber(@Param("trxNumber") String trxNumber);

    Transaction findTransactionByTrxNumber(String trxNumber);

    List<Transaction> findByCustId(Long custId);

    List<Transaction> findByCustIdAndGoalId(Long custId, Long goalId);

    List<Transaction> findByCustIdAndProductIdAndStatusOrderByCreatedDateAsc(Long custId, Long productId, String status);

    List<Transaction> findByCustIdAndGoalIdAndStatus(Long custId, Long goalId, String status);



}
