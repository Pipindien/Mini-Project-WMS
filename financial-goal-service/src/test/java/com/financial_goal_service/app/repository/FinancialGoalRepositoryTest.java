package com.financial_goal_service.app.repository;

import com.financial_goal_service.app.entity.FinancialGoal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class FinancialGoalRepositoryTest {

    @Autowired
    private FinancialGoalRepository financialGoalRepository;

    private FinancialGoal sampleGoal;

    @BeforeEach
    void setUp() {
        sampleGoal = new FinancialGoal();
        sampleGoal.setCustId(123L);
        sampleGoal.setGoalName("Beli Rumah");
        sampleGoal.setTargetAmount((double) 100000000L);
        sampleGoal.setCurrentAmount((double) 10000000L);
        sampleGoal.setStatus("ACTIVE");
        sampleGoal.setDeleted(false);
        financialGoalRepository.save(sampleGoal);
    }

    @Test
    public void testFindByGoalId() {

        Optional<FinancialGoal> fg = financialGoalRepository.findByGoalId(1L);
        Assertions.assertTrue(fg.isPresent());
        Assertions.assertEquals("ACTIVE", fg.get().getStatus());
    }

    @Test
    void findByGoalName() {
        Optional<FinancialGoal> found = financialGoalRepository.findByGoalName("Beli Rumah");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(123L, found.get().getCustId());
    }

    @Test
    void findByCustIdAndStatus() {
        List<FinancialGoal> goals = financialGoalRepository.findByCustIdAndStatus(123L, "ACTIVE");
        assertEquals(1, goals.size());
        assertEquals("Beli Rumah", goals.get(0).getGoalName());
    }

    @Test
    void updateCurrentAmount() {
        financialGoalRepository.updateCurrentAmount(sampleGoal.getGoalId(), (double) 10000000L);

        Optional<FinancialGoal> updated = financialGoalRepository.findByGoalId(sampleGoal.getGoalId());
        assertTrue(updated.isPresent());
        assertEquals(10000000L, updated.get().getCurrentAmount());
    }
}
