package com.financial_goal_service.app.client;

import com.financial_goal_service.app.client.dto.ProductResponse;
import com.financial_goal_service.app.client.dto.UsersResponse;
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
public class ProductClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${product-service.url}")
    private String productUrl;

    public List<ProductResponse> getProductByCategoryId (Long categoryId) {
        String url = UriComponentsBuilder.fromUriString(productUrl)
                .buildAndExpand(categoryId)
                .toUriString();

        System.out.println("Memanggil API product: " + url);

        try {
            ResponseEntity<ProductResponse[]> responseEntity = restTemplate.getForEntity(url, ProductResponse[].class);
            ProductResponse[] responseBody = responseEntity.getBody();

            if (responseBody == null || responseBody.length == 0) {
                System.out.println("Product dengan ID category tidak ditemukan untuk Id: " + categoryId);
                return List.of();
            }

            return Arrays.asList(responseBody);

        } catch (Exception e) {
            System.err.println("Error saat memanggil API Product: " + e.getMessage());
            return null;
        }
    }
}
