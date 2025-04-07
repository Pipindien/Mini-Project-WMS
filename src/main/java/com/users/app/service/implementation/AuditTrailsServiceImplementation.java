package com.users.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.users.app.dto.AuditTrailsRequest;
import com.users.app.dto.AuditTrailsResponse;
import com.users.app.entity.AuditTrails;
import com.users.app.repository.AuditTrailsRepository;
import com.users.app.service.AuditTrailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditTrailsServiceImplementation implements AuditTrailsService {


    @Autowired
    private AuditTrailsRepository auditTrailsRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AuditTrailsResponse insertAuditTrails(AuditTrailsRequest auditTrailsRequest) throws JsonProcessingException {

        AuditTrails trails = new AuditTrails();
        trails.setAction(auditTrailsRequest.getAction());
        trails.setDescription(auditTrailsRequest.getDescription());
        trails.setRequest(auditTrailsRequest.getRequest());
        trails.setResponse(auditTrailsRequest.getResponse());
        trails.setDate(new Date());
        AuditTrails saved = auditTrailsRepository.save(trails);

        AuditTrailsResponse response = new AuditTrailsResponse();
        response.setAction(trails.getAction());
        response.setDescription(trails.getDescription());
        response.setRequest(trails.getRequest());
        response.setResponse(trails.getResponse());
        response.setDate(saved.getDate());

        return response;
    }
}
