package com.transaction.app.repository;

import com.transaction.app.entity.Transaction;
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
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction trx1;
    private Transaction trx2;

    @Before
    public void setUp() {
        trx1 = new Transaction();
        trx1.setTrxNumber("TRX001");
        trx1.setStatus("PENDING");
        trx1.setAmount(1000.0);
        trx1.setLot(10);
        trx1.setCustId(1L);
        trx1.setProductId(101L);
        trx1.setGoalId(201L);
        trx1.setCreatedDate(new Date());
        trx1.setUpdateDate(new Date());

        trx2 = new Transaction();
        trx2.setTrxNumber("TRX002");
        trx2.setStatus("COMPLETED");
        trx2.setAmount(2000.0);
        trx2.setLot(5);
        trx2.setCustId(1L);
        trx2.setProductId(101L);
        trx2.setGoalId(201L);
        trx2.setCreatedDate(new Date());
        trx2.setUpdateDate(new Date());

        transactionRepository.save(trx1);
        transactionRepository.save(trx2);
    }

    @Test
    public void testFindByTrxNumber() {
        Transaction found = transactionRepository.findByTrxNumber("TRX001");
        assertNotNull(found);
        assertEquals("TRX001", found.getTrxNumber());
    }

    @Test
    public void testFindTransactionByTrxNumber() {
        Transaction found = transactionRepository.findTransactionByTrxNumber("TRX002");
        assertNotNull(found);
        assertEquals("TRX002", found.getTrxNumber());
    }

    @Test
    public void testFindByCustId() {
        List<Transaction> transactions = transactionRepository.findByCustId(1L);
        assertEquals(2, transactions.size());
    }

    @Test
    public void testFindByCustIdAndProductIdAndGoalIdAndStatusOrderByCreatedDateAsc() {
        List<Transaction> transactions = transactionRepository
                .findByCustIdAndProductIdAndGoalIdAndStatusOrderByCreatedDateAsc(1L, 101L, 201L, "PENDING");
        assertEquals(1, transactions.size());
        assertEquals("TRX001", transactions.get(0).getTrxNumber());
    }

    @Test
    public void testFindByCustIdAndGoalIdAndStatus() {
        List<Transaction> transactions = transactionRepository
                .findByCustIdAndGoalIdAndStatus(1L, 201L, "COMPLETED");
        assertEquals(1, transactions.size());
        assertEquals("TRX002", transactions.get(0).getTrxNumber());
    }
}
