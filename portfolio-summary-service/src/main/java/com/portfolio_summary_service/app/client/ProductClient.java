package com.portfolio_summary_service.app.client;

import com.portfolio_summary_service.app.client.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public ProductResponse getProductById (Long productId) {
        String url = UriComponentsBuilder.fromUriString(productUrl)
                .buildAndExpand(productId)
                .toUriString();

        System.out.println("Memanggil API product: " + url);

        try {
            ResponseEntity<ProductResponse> responseEntity = restTemplate.getForEntity(url, ProductResponse.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }
}
