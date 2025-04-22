package com.transaction.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.app.client.LogClient;
import com.transaction.app.client.dto.AuditTrailsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditTrailsServiceImplementationTest {

    @Mock
    private LogClient logClient;

    @InjectMocks
    private AuditTrailsServiceImplementation auditTrailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logsAuditTrails_shouldCreateAuditTrail() throws JsonProcessingException {
        // Arrange
        String action = "CREATE";
        String request = "TestRequest";
        String response = "TestResponse";
        String description = "This is a test";

        ArgumentCaptor<AuditTrailsRequest> captor = ArgumentCaptor.forClass(AuditTrailsRequest.class);

        // Act
        AuditTrailsRequest result = auditTrailsService.logsAuditTrails(action, request, response, description);

        // Assert
        verify(logClient, times(1)).createdAuditTrails(captor.capture());

        AuditTrailsRequest capturedRequest = captor.getValue();
        ObjectMapper mapper = new ObjectMapper();

        assertEquals(action, capturedRequest.getAction());
        assertNotNull(capturedRequest.getDate());
        assertEquals(mapper.writeValueAsString(request), capturedRequest.getRequest());
        assertEquals(mapper.writeValueAsString(response), capturedRequest.getResponse());
        assertEquals(description, capturedRequest.getDescription());

        // Also verify returned result
        assertEquals(capturedRequest.getAction(), result.getAction());
        assertEquals(capturedRequest.getRequest(), result.getRequest());
        assertEquals(capturedRequest.getResponse(), result.getResponse());
        assertEquals(capturedRequest.getDescription(), result.getDescription());
    }
}
