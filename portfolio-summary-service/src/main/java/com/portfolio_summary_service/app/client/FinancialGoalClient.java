package com.portfolio_summary_service.app.client;

import com.portfolio_summary_service.app.client.dto.FinancialGoalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FinancialGoalClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${financial-goal.url}")
    private String fingolUrl;

    public FinancialGoalResponse getFinansialGoalById(FinancialGoalResponse financialGoalResponse, String token) {
        try {
            String url = fingolUrl;

            HttpHeaders headers = new HttpHeaders();
            headers.set("token", token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<FinancialGoalResponse> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    FinancialGoalResponse.class,
                    financialGoalResponse.getGoalId()
            );

            FinancialGoalResponse responseBody = responseEntity.getBody();

            if (responseBody == null || responseBody.getGoalId() == null) {
                System.out.println("Financial Goal ID tidak ditemukan untuk: " + financialGoalResponse.getGoalName());
                return null;
            }

            return responseBody;

        } catch (Exception e) {
            System.err.println("Error saat memanggil API Financial Goal: " + e.getMessage());
            return null;
        }
    }
}
