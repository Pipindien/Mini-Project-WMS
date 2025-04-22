package com.transaction.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.client.dto.AuditTrailsRequest;

public interface AuditTrailsService {
    AuditTrailsRequest logsAuditTrails(String action, String transactionRequest, String transactionResponse, String description) throws JsonProcessingException;
}
