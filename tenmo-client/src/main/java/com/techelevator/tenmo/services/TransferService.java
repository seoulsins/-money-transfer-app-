package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class TransferService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public TransferService(String baseUrl) {
        this.baseUrl = baseUrl + "/transfers";
    }

    public List<Transfer> getTransferHistory() {
        HttpEntity<?> entity = makeAuthEntity();
        ResponseEntity<List<Transfer>> response = restTemplate.exchange(
                baseUrl + "/mytransfers",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Transfer>>() {
                }
        );
        return response.getBody();
    }

    public List<Transfer> getPendingTransfers(){
        HttpEntity<?> entity = makeAuthEntity();
        ResponseEntity<List<Transfer>> response = restTemplate.exchange(
                baseUrl + "/mytransfers/?status_is=pending",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Transfer>>() {
                }
        );
        return response.getBody();
    }

    public Transfer getTransferDetails(int transferId) {
        HttpEntity<?> entity = makeAuthEntity();
        ResponseEntity<Transfer> response = restTemplate.exchange(
                baseUrl + "/" + transferId,
                HttpMethod.GET,
                entity,
                Transfer.class
        );
        return response.getBody();
    }

    public Transfer postTransfer(Transfer transfer) {
        Transfer newTransfer = null;
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        newTransfer = restTemplate.postForObject(baseUrl, entity, Transfer.class);
        return newTransfer;
    }

    public boolean updateTransfer(Transfer transfer) {
        boolean success = false;
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        try {
            restTemplate.put(baseUrl + "/" + transfer.getTransferId(), entity);
            success = true;
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;

    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }
}



