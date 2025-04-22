package com.transaction.app.repository;

import com.transaction.app.dto.TransactionList;
import com.transaction.app.entity.Transaction;
import com.transaction.app.entity.TransactionHistory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionHistoryRepositoryTest {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transaction;
    private TransactionHistory history1;
    private TransactionHistory history2;

    @Before
    public void setUp() {
        transaction = new Transaction();
        transaction.setTrxNumber("TRX100");
        transaction.setStatus("ACTIVE");
        transaction.setAmount(5000.0);
        transaction.setLot(10);
        transaction.setCustId(1L);
        transaction.setProductId(1001L);
        transaction.setGoalId(2001L);
        transaction.setCreatedDate(new Date());
        transaction.setUpdateDate(new Date());
        transactionRepository.save(transaction);

        history1 = new TransactionHistory();
        history1.setStatus("PENDING");
        history1.setAmount(2500.0);
        history1.setLot(5);
        history1.setCustId(1L);
        history1.setProductId(1001L);
        history1.setGoalId(2001L);
        history1.setCreatedDate(new Date());
        history1.setNotes("First pending transaction");
        history1.setTransaction(transaction);
        transactionHistoryRepository.save(history1);

        history2 = new TransactionHistory();
        history2.setStatus("COMPLETED");
        history2.setAmount(2500.0);
        history2.setLot(5);
        history2.setCustId(1L);
        history2.setProductId(1001L);
        history2.setGoalId(2001L);
        history2.setCreatedDate(new Date());
        history2.setNotes("Second completed transaction");
        history2.setTransaction(transaction);
        transactionHistoryRepository.save(history2);
    }

    @Test
    public void testFindTransactionListByStatusAndCustId() {
        List<TransactionList> result = transactionHistoryRepository.findTransactionListByStatusAndCustId("PENDING", 1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        TransactionList item = result.get(0);
        assertEquals("TRX100", item.getTrxNumber());
        assertEquals("PENDING", item.getStatus());
        assertEquals(Long.valueOf(1L), item.getCustId());
        assertEquals(Integer.valueOf(5), item.getLot());
        assertEquals("First pending transaction", item.getNotes());
    }
}
