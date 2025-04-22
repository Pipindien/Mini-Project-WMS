package com.transaction.app.client;

import com.transaction.app.client.dto.ProductRequest;
import com.transaction.app.client.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProductClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.url}")
    private String productUrl;

    @Value("${product.urlById}")
    private String productUrlById;

    public ProductRequest getProductByProductByName(ProductRequest productRequest){
        String url = UriComponentsBuilder.fromUriString(productUrl)
                .buildAndExpand(productRequest.getProductName())
                .toUriString();

        System.out.println("Memanggil API Product: " + url);

        try {
            ResponseEntity<ProductRequest> responseEntity = restTemplate.getForEntity(url, ProductRequest.class);
            ProductRequest responseBody = responseEntity.getBody();

            System.out.println(" Response dari API Product: " + responseBody);

            if (responseBody == null || responseBody.getProductId() == null) {
                System.out.println("Product ID tidak ditemukan untuk nomor: " + productRequest.getProductId());
                return null;
            }

            return responseBody;

        } catch (Exception e) {
            System.err.println("Error saat memanggil API Product: " + e.getMessage());
            return null;
        }
    }

    public ProductResponse getProductById (Long productId) {
        String url = UriComponentsBuilder.fromUriString(productUrlById)
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
