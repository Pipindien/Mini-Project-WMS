package com.transaction.app.repository;

import com.transaction.app.entity.PortfolioSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PortfolioSummaryRepositoryTest {


    @Autowired
    private PortfolioSummaryRepository repository;

    private PortfolioSummary summary1;
    private PortfolioSummary summary2;

    @BeforeEach
    void setUp() {
        summary1 = new PortfolioSummary();
        summary1.setCustId(1L);
        summary1.setGoalId(100L);
        repository.save(summary1);

        summary2 = new PortfolioSummary();
        summary2.setCustId(1L);
        summary2.setGoalId(101L);
        repository.save(summary2);
    }

    @Test
    void findOneByGoalId() {
        Optional<PortfolioSummary> result = repository.findOneByGoalId(100L);
        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getGoalId());
    }

    @Test
    void findByCustIdAndGoalId() {
        Optional<PortfolioSummary> result = repository.findByCustIdAndGoalId(1L, 100L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getCustId());
        assertEquals(100L, result.get().getGoalId());
    }

    @Test
    void findAllByCustId() {
        List<PortfolioSummary> result = repository.findAllByCustId(1L);
        assertEquals(2, result.size());
    }
}