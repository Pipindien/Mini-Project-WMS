package com.transaction.app.client;

import com.transaction.app.client.dto.AuditTrailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;


@Service
public class LogClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${audit-trails.url}")
    private String logUrl;

    public AuditTrailsRequest createdAuditTrails(AuditTrailsRequest auditTrailsRequest){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuditTrailsRequest> httpEntity = new HttpEntity<>(auditTrailsRequest, httpHeaders);
        ResponseEntity<AuditTrailsRequest> responseEntity = restTemplate.postForEntity(
                logUrl,
                httpEntity,
                AuditTrailsRequest.class
        );
        return responseEntity.getBody();
    }
}
