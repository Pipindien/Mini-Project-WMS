package com.transaction.app.client;

import com.transaction.app.client.dto.UsersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class UsersClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${auth-validate-token.url}")
    private String authUrl;

    public Long getIdCustFromToken(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("token", token);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<UsersResponse> responseEntity = restTemplate.exchange(
                authUrl,
                HttpMethod.GET,
                httpEntity,
                UsersResponse.class
        );

        return responseEntity.getBody().getCustId();
    }

    public boolean updateUserBalance(String token, double balance, boolean isAddition) {
        Long custId = getIdCustFromToken(token);

        // Payload untuk update balance dengan flag isAddition
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", balance);
        payload.put("isAddition", isAddition);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("token", token); // Kirim token sebagai header

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, httpHeaders);

        String url = authUrl + "balance/" + custId;
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                String.class
        );

        return responseEntity.getStatusCode() == HttpStatus.OK;
    }

}
