package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class UserService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public UserService(String baseUrl) {
        this.baseUrl = baseUrl + "/accounts/";
    }

    public Account getMyAccount() {
        HttpEntity<Void> entity = makeAuthEntity();
        try {
            ResponseEntity<Account> response = restTemplate.exchange(
                    baseUrl + "myaccount",
                    HttpMethod.GET,
                    entity,
                    Account.class
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
            // Handle exception or log it
            return null;
        }
    }

    public Account getAccountByUserId(int userId) {
        HttpEntity<Void> entity = makeAuthEntity();

        try {
            ResponseEntity<Account> response = restTemplate.exchange(
                    baseUrl + userId,
                    HttpMethod.GET,
                    entity,
                    Account.class
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
            // Handle exception or log it
            return null;
        }
    }

//    public Account getAccountByAccountId(int accountId) {
//        HttpEntity<Void> entity = makeAuthEntity();
//        try {
//            ResponseEntity<Account> response = restTemplate.exchange(
//                    baseUrl + accountId,
//                    HttpMethod.GET,
//                    entity,
//                    Account.class
//            );
//            return response.getBody();
//        } catch (RestClientResponseException e) {
//            // Handle exception or log it
//            return null;
//        }
//    }

    public List<Account> getAllAccounts() {
        HttpEntity<Void> entity = makeAuthEntity();
        try {
            ResponseEntity<Account[]> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    entity,
                    Account[].class
            );
            return Arrays.asList(response.getBody());
        } catch (RestClientResponseException e) {
            // Handle exception or log it
            return null;
        }
    }
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
