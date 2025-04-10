package com.users.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.users.app.dto.AuditTrailsRequest;
import com.users.app.dto.AuditTrailsResponse;

public interface AuditTrailsService {
    AuditTrailsResponse insertAuditTrails(AuditTrailsRequest auditTrailsRequest) throws JsonProcessingException;
}
