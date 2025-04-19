package com.portfolio_summary_service.app.client;

import com.portfolio_summary_service.app.client.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
public class TransactionClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${transaction-my.url}")
    private String transactionUrl;

    public List<TransactionResponse> getMyTransactionsByGoal(String token, Long goalId) {
        String url = UriComponentsBuilder.fromUriString(transactionUrl)
                .buildAndExpand(goalId) // expand path variable {goalId}
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<TransactionResponse[]> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                TransactionResponse[].class
        );

        return Arrays.asList(responseEntity.getBody());
    }


}
