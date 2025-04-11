package com.transaction.app.repository;

import com.transaction.app.dto.TransactionList;
import com.transaction.app.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    @Query("SELECT new com.transaction.app.dto.TransactionList(t.trxNumber, th.id, th.amount, th.status, th.custId, th.lot, th.productId, th.goalId, th.notes, th.createdDate) FROM TransactionHistory th JOIN th.transaction t where th.status=:status")
    List<TransactionList> findTransactionListByStatus(@Param("status") String status);
}
