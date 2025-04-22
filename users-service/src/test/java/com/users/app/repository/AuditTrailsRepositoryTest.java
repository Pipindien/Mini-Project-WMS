package com.users.app.repository;

import com.users.app.entity.AuditTrails;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AuditTrailsRepositoryTest {

    @Autowired
    private AuditTrailsRepository auditTrailsRepository;

    @Test
    public void testSaveAndFindById() {
        // Arrange
        AuditTrails audit = new AuditTrails();
        audit.setAction("LOGIN");
        audit.setDescription("User login attempt");
        audit.setDate(new Date());
        audit.setRequest("{\"username\":\"testuser\"}");
        audit.setResponse("{\"status\":\"success\"}");

        // Act
        AuditTrails savedAudit = auditTrailsRepository.save(audit);
        Optional<AuditTrails> retrievedAuditOpt = auditTrailsRepository.findById(savedAudit.getId());

        // Assert
        Assert.assertTrue(retrievedAuditOpt.isPresent());
        AuditTrails retrievedAudit = retrievedAuditOpt.get();
        Assert.assertEquals("LOGIN", retrievedAudit.getAction());
        Assert.assertEquals("User login attempt", retrievedAudit.getDescription());
        Assert.assertEquals("{\"username\":\"testuser\"}", retrievedAudit.getRequest());
        Assert.assertEquals("{\"status\":\"success\"}", retrievedAudit.getResponse());
    }
}
