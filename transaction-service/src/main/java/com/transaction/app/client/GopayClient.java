package com.transaction.app.client;

import com.transaction.app.client.dto.GopayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Random;

@Service
public class GopayClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${gopay-transaction.url}")
    private String gopayTransactionUrl;

    @Value("${gopay-status.url}")
    private String gopayStatusUrl;

    public GopayResponse getGopayTransaction(GopayResponse gopayResponse) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GopayResponse> httpEntity = new HttpEntity<>(gopayResponse, httpHeaders);
        ResponseEntity<GopayResponse> responseEntity = restTemplate.postForEntity(
                gopayTransactionUrl,
                httpEntity,
                GopayResponse.class
        );
        return responseEntity.getBody();
    }

    public GopayResponse getGopayStatus(GopayResponse gopayResponse) {
        String url = UriComponentsBuilder.fromUriString(gopayStatusUrl)
                .buildAndExpand(gopayResponse.getPhone())
                .toUriString();

        System.out.println("Memanggil API gopay status: " + url);

        try {
            ResponseEntity<GopayResponse> responseEntity = restTemplate.getForEntity(url, GopayResponse.class);
            GopayResponse responseBody = responseEntity.getBody();

            if (responseBody == null || responseBody.getPhone() == null) {
                System.out.println("Phone tidak ditemukan untuk nomor: " + gopayResponse.getPhone());
                return null;
            }

            String[] possibleStatuses = {"PENDING", "FAILED", "SUCCESS"};
            String randomizedStatus = possibleStatuses[new Random().nextInt(possibleStatuses.length)];
            responseBody.setStatus(randomizedStatus);

            System.out.println("Status diganti secara acak menjadi: " + randomizedStatus);
            return responseBody;

        } catch (Exception e) {
            System.err.println("Error saat memanggil API gopay: " + e.getMessage());
            return null;
        }
    }


}
