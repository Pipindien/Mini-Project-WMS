package com.users.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.users.app.dto.AuditTrailsRequest;
import com.users.app.dto.AuditTrailsResponse;
import com.users.app.entity.AuditTrails;
import com.users.app.repository.AuditTrailsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuditTrailsServiceImplementationTest {

    @InjectMocks
    private AuditTrailsServiceImplementation auditTrailsService;

    @Mock
    private AuditTrailsRepository auditTrailsRepository;

    private AuditTrailsRequest auditTrailsRequest;

    @Before
    public void setUp() {
        // Persiapkan data yang diperlukan
        auditTrailsRequest = AuditTrailsRequest.builder()
                .action("CREATE")
                .description("Create new user")
                .request("{\"username\": \"testuser\", \"password\": \"password123\"}")
                .response("{\"status\": \"success\"}")
                .date(new Date())
                .build();
    }

    @Test
    public void testInsertAuditTrails() throws JsonProcessingException {

        AuditTrails savedAuditTrail = new AuditTrails();
        savedAuditTrail.setId(1L);
        savedAuditTrail.setAction(auditTrailsRequest.getAction());
        savedAuditTrail.setDescription(auditTrailsRequest.getDescription());
        savedAuditTrail.setRequest(auditTrailsRequest.getRequest());
        savedAuditTrail.setResponse(auditTrailsRequest.getResponse());
        savedAuditTrail.setDate(new Date());

        when(auditTrailsRepository.save(any(AuditTrails.class))).thenReturn(savedAuditTrail);

        AuditTrailsResponse response = auditTrailsService.insertAuditTrails(auditTrailsRequest);

        assertNotNull(response);
        assertEquals(savedAuditTrail.getAction(), response.getAction());
        assertEquals(savedAuditTrail.getDescription(), response.getDescription());
        assertEquals(savedAuditTrail.getRequest(), response.getRequest());
        assertEquals(savedAuditTrail.getResponse(), response.getResponse());
        assertNotNull(response.getDate());

        verify(auditTrailsRepository, times(1)).save(any(AuditTrails.class));
    }

}
