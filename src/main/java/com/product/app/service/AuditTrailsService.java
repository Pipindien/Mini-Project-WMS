package com.product.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.app.client.LogClient;
import com.product.app.client.dto.AuditTrailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditTrailsService {

    @Autowired
    private LogClient logClient;

    private final ObjectMapper mapper = new ObjectMapper();

    public AuditTrailsRequest logsAuditTrails(String action, String productRequest, String productResponse, String description) throws JsonProcessingException {
        AuditTrailsRequest auditTrailsRequest = new AuditTrailsRequest();
        auditTrailsRequest.setAction(action);
        auditTrailsRequest.setDate(new Date());
        auditTrailsRequest.setRequest(mapper.writeValueAsString(productRequest));
        auditTrailsRequest.setResponse(mapper.writeValueAsString(productResponse));
        auditTrailsRequest.setDescription(description);
        logClient.createdAuditTrails(auditTrailsRequest);
        return auditTrailsRequest;
    }
}