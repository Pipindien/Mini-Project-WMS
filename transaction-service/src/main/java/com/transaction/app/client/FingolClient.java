package com.transaction.app.client;

import com.transaction.app.client.dto.FinancialGoalResponse;
import com.transaction.app.client.dto.UpdateProgressRequest;
import com.transaction.app.dto.insight.InsightResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class FingolClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${financial-goal.url}")
    private String fingolUrl;

    @Value("${financial-goal.urlUpdate}")
    private String fingolUrlUpdate;

    @Value("${financial-goal.urlById}")
    private String fingolUrlById;

    @Value("${financial-goal.urlGoalByIdCustomer}")
    private String fingolUrlGoalByIdCustomer;

    @Value("${financial-goal.urlByIdWithOutDelete}")
    private String fingolUrlGoalByIdWithOutDelete;


    public FinancialGoalResponse getFinansialGoalByName(FinancialGoalResponse financialGoalResponse, String token) {
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
                    financialGoalResponse.getGoalName()
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

    public String updateCurrentAmountAndInsight(Long goalId, Double amount, String insightMessage, String token) {
        try {
            String url = fingolUrlUpdate.replace("{goalId}", String.valueOf(goalId));

            // 1. Build body
            UpdateProgressRequest request = new UpdateProgressRequest();
            request.setCurrentAmount(amount);
            request.setInsightMessage(insightMessage);

            HttpHeaders headers = new HttpHeaders();
            headers.set("token", token);
            HttpEntity<UpdateProgressRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            return "Error updating progress: " + e.getMessage();
        }
    }

    public List<FinancialGoalResponse> getGoalsByCustId(String token) {
        try {
            String url = fingolUrlGoalByIdCustomer;

            HttpHeaders headers = new HttpHeaders();
            headers.set("token", token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<FinancialGoalResponse>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<FinancialGoalResponse>>() {}
            );

            return responseEntity.getBody();

        } catch (Exception e) {
            System.err.println("Error saat memanggil API Financial Goal List: " + e.getMessage());
            return Collections.emptyList(); // return list kosong kalau gagal
        }
    }


    public FinancialGoalResponse getFinancialGoalById(Long goalId, String token) {

            String url = fingolUrlById.replace("{goalId}", String.valueOf(goalId));

            HttpHeaders headers = new HttpHeaders();
            headers.set("token", token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Perform the API call to get the financial goal
            ResponseEntity<FinancialGoalResponse> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    FinancialGoalResponse.class
            );

            FinancialGoalResponse responseBody = responseEntity.getBody();
            return responseBody;
    }

    public FinancialGoalResponse getFinancialGoalByIdWithOutDelete(Long goalId, String token) {

        String url = fingolUrlGoalByIdWithOutDelete.replace("{goalId}", String.valueOf(goalId));

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Perform the API call to get the financial goal
        ResponseEntity<FinancialGoalResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                FinancialGoalResponse.class
        );

        FinancialGoalResponse responseBody = responseEntity.getBody();
        return responseBody;
    }

}