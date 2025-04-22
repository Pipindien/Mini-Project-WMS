package com.financial_goal_service.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial_goal_service.app.client.LogClient;
import com.financial_goal_service.app.client.dto.AuditTrailsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditTrailsServiceTest {

    @Mock
    private LogClient logClient;

    @InjectMocks
    private AuditTrailsService auditTrailsService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void logsAuditTrails() throws JsonProcessingException {
        // Arrange
        String action = "CREATE_PRODUCT";
        String productRequest = "{\"name\": \"Product1\", \"price\": 100.0}";
        String productResponse = "{\"id\": 1, \"name\": \"Product1\", \"price\": 100.0}";
        String description = "Created new product";

        // Create an expected AuditTrailsRequest object
        AuditTrailsRequest expectedAuditTrailsRequest = new AuditTrailsRequest();
        expectedAuditTrailsRequest.setAction(action);
        expectedAuditTrailsRequest.setDate(new Date());
        expectedAuditTrailsRequest.setRequest(objectMapper.writeValueAsString(productRequest));
        expectedAuditTrailsRequest.setResponse(objectMapper.writeValueAsString(productResponse));
        expectedAuditTrailsRequest.setDescription(description);

        // Act
        AuditTrailsRequest result = auditTrailsService.logsAuditTrails(action, productRequest, productResponse, description);

        // Assert
        assertNotNull(result);
        assertEquals(action, result.getAction());
        assertEquals(description, result.getDescription());
        assertEquals(objectMapper.writeValueAsString(productRequest), result.getRequest());
        assertEquals(objectMapper.writeValueAsString(productResponse), result.getResponse());

        // Verify interaction with LogClient
        verify(logClient).createdAuditTrails(result);
    }
}