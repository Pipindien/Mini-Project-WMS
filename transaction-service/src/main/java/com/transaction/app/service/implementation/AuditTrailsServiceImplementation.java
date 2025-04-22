package com.transaction.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.app.client.LogClient;
import com.transaction.app.client.dto.AuditTrailsRequest;
import com.transaction.app.service.AuditTrailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditTrailsServiceImplementation implements AuditTrailsService {

    @Autowired
    private LogClient logClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AuditTrailsRequest logsAuditTrails(String action, String transactionRequest, String transactionResponse, String description) throws JsonProcessingException {
        AuditTrailsRequest auditTrailsRequest = new AuditTrailsRequest();
        auditTrailsRequest.setAction(action);
        auditTrailsRequest.setDate(new Date());
        auditTrailsRequest.setRequest(mapper.writeValueAsString(transactionRequest));
        auditTrailsRequest.setResponse(mapper.writeValueAsString(transactionResponse));
        auditTrailsRequest.setDescription(description);
        logClient.createdAuditTrails(auditTrailsRequest);
        return auditTrailsRequest;
    }
}
