package com.users.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.users.app.dto.AuditTrailsRequest;
import com.users.app.dto.AuditTrailsResponse;
import com.users.app.service.implementation.AuditTrailsServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/audit-trails")
public class AuditTrailsController {
    @Autowired
    private AuditTrailsServiceImplementation auditTrailsServiceImplementation;

    @PostMapping("/save")
    public ResponseEntity<AuditTrailsResponse> saveActivity(@RequestBody AuditTrailsRequest request) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(auditTrailsServiceImplementation.insertAuditTrails(request));
    }
}
