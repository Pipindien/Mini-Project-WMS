package com.financial_goal_service.app.client;

import com.financial_goal_service.app.client.dto.UsersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthClientService {

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
                UsersResponse.class);
        return responseEntity.getBody().getCustId();
    }


}
