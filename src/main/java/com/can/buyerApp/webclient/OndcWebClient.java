package com.can.buyerApp.webclient;

import com.can.buyerApp.dto.Acknowledgement;
import com.can.buyerApp.dto.ResponseDTO;
import com.can.buyerApp.request.*;
import com.can.buyerApp.utils.Routes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Objects;

@Slf4j
@Component
public class OndcWebClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OndcWebClient(RestTemplate restTemplate, ObjectMapper objectMapper)
    {
        this.restTemplate = restTemplate;
        this.objectMapper=objectMapper;
    }

    @Value("${header.privateKey}")
    private String privateKey;

    @Value("${header.subscriberId}")
    private String subscriberId;

    @Value("${header.uniqueKeyId}")
    private String uniqueKeyId;

    @Value("${api.search.url}")
    private String apiSearchUrl;

    /**
     * Sends first motor search request to ONDC gateway
     * @param searchRequest MotorSearchRequest object
     * @return ResponseEntity with acknowledgement
     */
    public ResponseEntity<?> sendFirstMotorSearchRequest(MotorSearchRequest searchRequest) {
        String messageId = searchRequest.getContext().getMessage_id();
        String transactionId = searchRequest.getContext().getTransaction_id();
        String value;

        try {
            value = objectMapper.writeValueAsString(searchRequest);
        } catch (JsonProcessingException e) {
            log.error("Error serializing motor search request: ", e);
            throw new RuntimeException("Error serializing motor search request", e);
        }

        String signatureHeader;
        try {
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for First Motor Search Message ID: {}, Transaction ID: {}",
                    messageId, transactionId);
        } catch (Exception e) {
            log.error("Error generating signature header for motor search: ", e);
            throw new RuntimeException("Error generating signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);
        HttpEntity<MotorSearchRequest> requestEntity = new HttpEntity<>(searchRequest, headers);

        try {
            String jsonRequest = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(searchRequest);

            log.info("Motor Search Request Body:\n{}", jsonRequest);
            log.info("Motor Search Request Headers: {}", headers);

        } catch (JsonProcessingException e) {
            log.error("Failed to convert request to JSON", e);
        }
        try {
            log.info("Sending first motor search request to URL: {}, Message ID: {}, Transaction ID: {}",
                    apiSearchUrl, messageId, transactionId);

            ResponseEntity<?> responseEntity = restTemplate.exchange(
                    apiSearchUrl, HttpMethod.POST, requestEntity, Acknowledgement.class);

            log.info("Received response for first motor search: Message ID: {}, Transaction ID: {}, Response: {}",
                    messageId, transactionId, responseEntity.getBody());

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .action(searchRequest.getContext().getAction())
                    .transactionId(searchRequest.getContext().getTransaction_id())
                    .messageId(searchRequest.getContext().getMessage_id())
                    .bapId(searchRequest.getContext().getBap_id())
                    .bapUri(searchRequest.getContext().getBap_uri())
                    .build();

            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("An error occurred while processing first motor search request: {}, Message ID: {}, Transaction ID: {}",
                    e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }

    /**
     * Sends second motor search request with vehicle details to BPP
     * @param searchRequest MotorSearchRequest object
     * @return ResponseEntity with acknowledgement
     */
    public ResponseEntity<?> sendSecondMotorSearch(MotorSearchRequest searchRequest) {
        MotorSearchRequest.Context context = searchRequest.getContext();

        if (Objects.isNull(context)) {
            log.error("MotorSearchRequest context is null. Unable to proceed.");
            throw new IllegalArgumentException("MotorSearchRequest context cannot be null.");
        }

        String messageId = context.getMessage_id();
        String transactionId = context.getTransaction_id();
        String value;

        try {
            value = objectMapper.writeValueAsString(searchRequest);
        } catch (JsonProcessingException e) {
            log.error("Error serializing second motor search request: ", e);
            throw new RuntimeException("Error serializing second motor search request", e);
        }

        String signatureHeader;
        try {
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for second motor search Message ID: {}, Transaction ID: {}",
                    messageId, transactionId);
        } catch (Exception e) {
            log.error("Error while generating signature header for second motor search Message ID: {}, Transaction ID: {}, Error: {}",
                    messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);
        HttpEntity<MotorSearchRequest> requestEntity = new HttpEntity<>(searchRequest, headers);

        String url = searchRequest.getContext().getBpp_uri() + searchRequest.getContext().getAction();

        try {
            log.info("Sending second motor search request to URL: {}, Message ID: {}, Transaction ID: {}",
                    url, messageId, transactionId);

            ResponseEntity<?> responseEntity = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, Acknowledgement.class);

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .action(searchRequest.getContext().getAction())
                    .transactionId(searchRequest.getContext().getTransaction_id())
                    .messageId(searchRequest.getContext().getMessage_id())
                    .bapId(searchRequest.getContext().getBap_id())
                    .bapUri(searchRequest.getContext().getBap_uri())
                    .build();

            log.info("Received response for second motor search: Message ID: {}, Transaction ID: {}, Response: {}",
                    messageId, transactionId, responseEntity.getBody());

            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("An error occurred while processing second motor search: {}, Message ID: {}, Transaction ID: {}",
                    e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }


    /**
     * Sends motor select request with vehicle details to BPP
     * @param selectRequest MotorSelectRequest object
     * @return ResponseEntity with acknowledgement
     */
    public ResponseEntity<?> sendMotorSelect(MotorSelectRequest selectRequest){

        MotorSelectRequest.Context context = selectRequest.getContext();

        if (Objects.isNull(context)) {
            log.error("SelectRequest context is null. Unable to proceed.");
            throw new IllegalArgumentException("SearchRequest context cannot be null.");
        }
        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId=context.getTransaction_id();
        String url = selectRequest.getContext().getBpp_uri()+selectRequest.getContext().getAction();

        if (org.apache.commons.lang3.StringUtils.isEmpty(bapId) || org.apache.commons.lang3.StringUtils.isEmpty(messageId) || StringUtils.isEmpty(transactionId)) {
            log.error("Missing required fields so unable to send select request: BAP ID: {}, Message ID: {}, Transaction ID: {}", bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String signatureHeader;

        try {
            ObjectMapper objectMapper=new ObjectMapper();
            String value = objectMapper.writeValueAsString(selectRequest);

            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for select request for Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);
        }
        catch (Exception e) {
            log.error("Error while generating Message ID: {}, TransactionEntity ID: {}, Error: {}",
                    messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);
        HttpEntity<MotorSelectRequest> requestEntity = new HttpEntity<>(selectRequest, headers);

        try {
            log.info("Sending Select request to URL: {}, Message ID: {}, TransactionEntity ID: {}",
                    url, messageId, transactionId);

            ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);
            log.info("Received response for select: Message ID: {}, TransactionEntity ID: {}, Response: {}",
                    messageId, transactionId, responseEntity.getBody());

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(selectRequest.getContext().getAction())
                    .transactionId(selectRequest.getContext().getTransaction_id())
                    .messageId(selectRequest.getContext().getMessage_id())
                    .bapId(selectRequest.getContext().getBap_id())
                    .bapUri(selectRequest.getContext().getBap_uri())
                    .build();

            return ResponseEntity.ok(responseDTO);
        }
        catch (Exception e) {
            log.error("An unexpected error occurred: {}, Message ID: {}, TransactionEntity ID: {}",
                    e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }

    public ResponseEntity<?> sendFirstSearchRequest(SearchRequest searchRequest){

        String messageId = searchRequest.getContext().getMessage_id();
        String transactionId = searchRequest.getContext().getTransaction_id();
        String value;
        try {
            value= objectMapper.writeValueAsString(searchRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String signatureHeader;

        try {
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
            log.info("SignatureHeader created successfully for First Search Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);
        HttpEntity<SearchRequest> requestEntity = new HttpEntity<>(searchRequest, headers);

        try {
            log.info("Sending search request to URL: {}, Message ID: {}, TransactionEntity ID: {}", apiSearchUrl, messageId, transactionId);
           ResponseEntity<?> responseEntity = restTemplate.exchange(apiSearchUrl, HttpMethod.POST, requestEntity, Acknowledgement.class);
           log.info("Received response for first search: Message ID: {}, TransactionEntity ID: {}, Response: {}", messageId, transactionId, responseEntity.getBody());

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(searchRequest.getContext().getAction())
                    .transactionId(searchRequest.getContext().getTransaction_id())
                    .messageId(searchRequest.getContext().getMessage_id())
                    .bapId(searchRequest.getContext().getBap_id())
                    .bapUri(searchRequest.getContext().getBap_uri())
                    .build();

            return ResponseEntity.ok(responseDTO);
        }
        catch (Exception e) {
            log.error("An error occurred while processing first search request : {}, Message ID: {}, TransactionEntity ID: {}", e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }

    public ResponseEntity<?> sendSecondSearch(SearchRequest searchRequest){
        SearchRequest.Context context = searchRequest.getContext();

        if (Objects.isNull(context)) {
            log.error("SearchRequest context is null. Unable to proceed.");
            throw new IllegalArgumentException("SearchRequest context cannot be null.");
        }

        String messageId = context.getMessage_id();
        String transactionId= context.getTransaction_id();
        String value;

        try {
            value= objectMapper.writeValueAsString(searchRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String signatureHeader;
        try {
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully second search for Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);
        }
        catch (Exception e) {
            log.error("Error while generating signature header with Message ID: {}, TransactionEntity ID: {}, Error: {}",messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);
        HttpEntity<SearchRequest> requestEntity = new HttpEntity<>(searchRequest, headers);
        String url=searchRequest.getContext().getBpp_uri()+searchRequest.getContext().getAction();
        try {
            log.info("Sending second search request to URL: {}, Message ID: {}, TransactionEntity ID: {}", url, messageId, transactionId);
            ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(searchRequest.getContext().getAction())
                    .transactionId(searchRequest.getContext().getTransaction_id())
                    .messageId(searchRequest.getContext().getMessage_id())
                    .bapId(searchRequest.getContext().getBap_id())
                    .bapUri(searchRequest.getContext().getBap_uri())
                    .build();
            log.info("Received response for second search: Message ID: {}, TransactionEntity ID: {}, Response: {}", messageId, transactionId, responseEntity.getBody());
            return ResponseEntity.ok(responseDTO);

        }
        catch (Exception e) {
            log.error("An error occurred while processing second search: {}, Message ID: {}, TransactionEntity ID: {}", e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }

    public ResponseEntity<?> sendSelect(SelectRequest selectRequest){

        SelectRequest.Context context = selectRequest.getContext();

        if (Objects.isNull(context)) {
            log.error("SelectRequest context is null. Unable to proceed.");
            throw new IllegalArgumentException("SearchRequest context cannot be null.");
        }
        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId=context.getTransaction_id();
        String url = selectRequest.getContext().getBpp_uri()+selectRequest.getContext().getAction();

        if (org.apache.commons.lang3.StringUtils.isEmpty(bapId) || org.apache.commons.lang3.StringUtils.isEmpty(messageId) || StringUtils.isEmpty(transactionId)) {
            log.error("Missing required fields so unable to send select request: BAP ID: {}, Message ID: {}, Transaction ID: {}", bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String signatureHeader;

        try {
            ObjectMapper objectMapper=new ObjectMapper();
            String value = objectMapper.writeValueAsString(selectRequest);

            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for select request for Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);
        }
        catch (Exception e) {
            log.error("Error while generating Message ID: {}, TransactionEntity ID: {}, Error: {}",
                    messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);
        HttpEntity<SelectRequest> requestEntity = new HttpEntity<>(selectRequest, headers);

        try {
            log.info("Sending Select request to URL: {}, Message ID: {}, TransactionEntity ID: {}",
                    url, messageId, transactionId);

            ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);
            log.info("Received response for select: Message ID: {}, TransactionEntity ID: {}, Response: {}",
                    messageId, transactionId, responseEntity.getBody());

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(selectRequest.getContext().getAction())
                    .transactionId(selectRequest.getContext().getTransaction_id())
                    .messageId(selectRequest.getContext().getMessage_id())
                    .bapId(selectRequest.getContext().getBap_id())
                    .bapUri(selectRequest.getContext().getBap_uri())
                    .build();

            return ResponseEntity.ok(responseDTO);
        }
        catch (Exception e) {
            log.error("An unexpected error occurred: {}, Message ID: {}, TransactionEntity ID: {}",
                    e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }


    public ResponseEntity<?> sendFirstInitRequest( InitRequest initRequest){

        InitRequest.Context context = initRequest.getContext();

        if (Objects.isNull(context)) {
            log.error("InitRequest context is null. Unable to proceed.");
            throw new IllegalArgumentException("InitRequest context cannot be null.");
        }
        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId=context.getTransaction_id();
        String url = initRequest.getContext().getBpp_uri()+initRequest.getContext().getAction();

        if (StringUtils.isEmpty(bapId) || StringUtils.isEmpty(messageId) || StringUtils.isEmpty(transactionId)) {
            log.error("Missing required fields so unable to send first init: BAP ID: {}, Message ID: {}, Transaction ID: {}", bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String signatureHeader;
        try {
            ObjectMapper objectMapper=new ObjectMapper();
            String value = objectMapper.writeValueAsString(initRequest);

            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for first init Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);
        }
        catch (Exception e) {
            log.error("Error while generating signature header with Message ID: {}, TransactionEntity ID: {}, Error: {}", messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);
        HttpEntity<InitRequest> requestEntity = new HttpEntity<>(initRequest, headers);

        try {
            log.info("Sending First init request to URL: {}, Message ID: {}, TransactionEntity ID: {}", url, messageId, transactionId);

            ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);
            log.info("Received response for first init: Message ID: {}, TransactionEntity ID: {}, Response: {}", messageId, transactionId, responseEntity.getBody());

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(initRequest.getContext().getAction())
                    .transactionId(initRequest.getContext().getTransaction_id())
                    .messageId(initRequest.getContext().getMessage_id())
                    .bapId(initRequest.getContext().getBap_id())
                    .bapUri(initRequest.getContext().getBap_uri())
                    .build();
            return ResponseEntity.ok(responseDTO);

        }
        catch (Exception e) {
            log.error("An error occurred while sending first init request: {}, Message ID: {}, TransactionEntity ID: {}", e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }

    public ResponseEntity<?> sendSecondInitRequest(SecondInitRequest secondInitRequest){

        SecondInitRequest.Context context = secondInitRequest.getContext();

        if (Objects.isNull(context)) {
            log.error("SecondInitRequest context is null. Unable to proceed.");
            throw new IllegalArgumentException("SecondInitRequest context cannot be null.");
        }
        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId=context.getTransaction_id();
        String url = secondInitRequest.getContext().getBpp_uri()+secondInitRequest.getContext().getAction();

        if (StringUtils.isEmpty(bapId) || StringUtils.isEmpty(messageId) || StringUtils.isEmpty(transactionId)) {
            log.error("Missing required fields so unable to send second init: BAP ID: {}, Message ID: {}, Transaction ID: {}", bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String signatureHeader;
        try {
            ObjectMapper objectMapper=new ObjectMapper();
            String value = objectMapper.writeValueAsString(secondInitRequest);
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
        }
        catch (Exception e) {
            log.error("Error while generating signature header with Message ID: {}, TransactionEntity ID: {}, Error: {}", messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);
        HttpEntity<SecondInitRequest> requestEntity = new HttpEntity<>(secondInitRequest, headers);
        try {
            log.info("Sending second init request to URL: {}, Message ID: {}, TransactionEntity ID: {}",url, messageId, transactionId);
            ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);
            log.info("Received response for second init: Message ID: {}, TransactionEntity ID: {}, Response: {}", messageId, transactionId, responseEntity.getBody());

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(secondInitRequest.getContext().getAction())
                    .transactionId(secondInitRequest.getContext().getTransaction_id())
                    .messageId(secondInitRequest.getContext().getMessage_id())
                    .bapId(secondInitRequest.getContext().getBap_id())
                    .bapUri(secondInitRequest.getContext().getBap_uri())
                    .build();

            return ResponseEntity.ok(responseDTO);

        }
        catch (Exception e) {
            log.error("An error occurred while sending second init request: {}, Message ID: {}, TransactionEntity ID: {}", e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }

    public ResponseEntity<?> sendConfirm(MotorConfirmRequest confirmRequest){

        MotorConfirmRequest.Context context = confirmRequest.getContext();

        if (Objects.isNull(context)) {
            log.error("Confirm Request context is null. Unable to proceed.");
            throw new IllegalArgumentException("Confirm Request context cannot be null.");
        }
        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId= context.getTransaction_id();
        String url = confirmRequest.getContext().getBpp_uri()+confirmRequest.getContext().getAction();

        if (Objects.isNull(bapId) || Objects.isNull(messageId) || Objects.isNull(transactionId)) {
            log.error("Missing required fields so unable to send confirm request: BAP ID: {}, Message ID: {}, Transaction ID: {}", bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String signatureHeader;
        try {
            ObjectMapper objectMapper=new ObjectMapper();
            String value = objectMapper.writeValueAsString(confirmRequest);
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for confirm request with Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);
        }
        catch (Exception e) {
            log.error("Error while generating signature header:  Message ID: {}, TransactionEntity ID: {}, Error: {}", messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);
        HttpEntity<MotorConfirmRequest> requestEntity = new HttpEntity<>(confirmRequest, headers);

        try {
            log.info("Sending Confirm request to URL: {}, Message ID: {}, TransactionEntity ID: {}", url, messageId, transactionId);
            ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);
            log.info("Received response for confirm: Message ID: {}, TransactionEntity ID: {}, Response: {}", messageId, transactionId, responseEntity.getBody());

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(confirmRequest.getContext().getAction())
                    .transactionId(confirmRequest.getContext().getTransaction_id())
                    .messageId(confirmRequest.getContext().getMessage_id())
                    .bapId(confirmRequest.getContext().getBap_id())
                    .bapUri(confirmRequest.getContext().getBap_uri())
                    .build();

            return ResponseEntity.ok(responseDTO);

        }
        catch (Exception e) {
            log.error("An error occurred while processing confirm request: {}, Message ID: {}, TransactionEntity ID: {}", e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }

    public ResponseEntity<?> sendStatus(MotorStatusRequest statusRequest){

        MotorStatusRequest.Context context = statusRequest.getContext();

        if (Objects.isNull(context)) {
            log.error("MotorStatusRequest context is null. Unable to proceed.");
            throw new IllegalArgumentException("MotorStatusRequest context cannot be null.");
        }
        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId= context.getTransaction_id();
        String url=statusRequest.getContext().getBpp_uri()+statusRequest.getContext().getAction();

        if (StringUtils.isEmpty(bapId) || StringUtils.isEmpty(messageId) || StringUtils.isEmpty(transactionId)) {
            log.error("Missing required fields so unable to send status request: BAP ID: {}, Message ID: {}, Transaction ID: {}", bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String signatureHeader;
        try {
            String value = objectMapper.writeValueAsString(statusRequest);
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for Status request with Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);
        }
        catch (Exception e) {
            log.error("Error while generating signature header with Message ID: {}, TransactionEntity ID: {}, Error: {}", messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);
        HttpEntity<MotorStatusRequest> requestEntity = new HttpEntity<>(statusRequest, headers);

        try {
            log.info("Sending status request to URL: {}, Message ID: {}, TransactionEntity ID: {}", url, messageId, transactionId);
            ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);
            log.info("Received response for status request: Message ID: {}, TransactionEntity ID: {}, Response: {}", messageId, transactionId, responseEntity.getBody());

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(statusRequest.getContext().getAction())
                    .transactionId(statusRequest.getContext().getTransaction_id())
                    .messageId(statusRequest.getContext().getMessage_id())
                    .bapId(statusRequest.getContext().getBap_id())
                    .bapUri(statusRequest.getContext().getBap_uri())
                    .build();
            return ResponseEntity.ok(responseDTO);
        }
        catch (Exception e) {
            log.error("An error occurred while sending status request: {}, Message ID: {}, TransactionEntity ID: {}", e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }

    public ResponseEntity<?> sendCancel(CancelRequest cancelRequest){

        CancelRequest.Context context = cancelRequest.getContext();
        if (Objects.isNull(context)) {
            log.error("cancelRequest context is null. Unable to proceed.");
            throw new IllegalArgumentException("cancelRequest context cannot be null.");
        }

        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId= context.getTransaction_id();
        String url=cancelRequest.getContext().getBpp_uri()+cancelRequest.getContext().getAction();

        if (Objects.isNull(bapId) || Objects.isNull(messageId) || Objects.isNull(transactionId)) {
            log.error("Missing required fields so unable to send cancel request: BAP ID: {}, Message ID: {}, Transaction ID: {}", bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String signatureHeader;

        try {
            String value=objectMapper.writeValueAsString(cancelRequest);
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for cancel request with Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);
        }
        catch (Exception e) {
            log.error("Error while  generating signature header Message ID: {}, TransactionEntity ID: {}, Error: {}",
                    messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);

        HttpEntity<CancelRequest> requestEntity = new HttpEntity<>(cancelRequest, headers);

        try {
            log.info("Sending cancel request to URL: {}, Message ID: {}, TransactionEntity ID: {}",
                    url, messageId, transactionId);
            ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);
            log.info("Received response for On Cancel Request");

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(cancelRequest.getContext().getAction())
                    .transactionId(cancelRequest.getContext().getTransaction_id())
                    .messageId(cancelRequest.getContext().getMessage_id())
                    .bapId(cancelRequest.getContext().getBap_id())
                    .bapUri(cancelRequest.getContext().getBap_uri())
                    .build();
            return ResponseEntity.ok(responseDTO);
        }
        catch (Exception e) {
            log.error("An error occurred while sending cancel request: {}, Message ID: {}, TransactionEntity ID: {}",
                    e.getMessage(), messageId, transactionId, e);
            throw e;
        }
    }

    public ResponseEntity<?> sendIssue(IssueRequest issuePayload) {
        IssueRequest.Context context = issuePayload.getContext();
        if (Objects.isNull(context)) {
            log.error("issueRequest context is null. Unable to proceed.");
            throw new IllegalArgumentException("issueRequest context cannot be null.");
        }

        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId= context.getTransaction_id();
        String url=issuePayload.getContext().getBpp_uri()+issuePayload.getContext().getAction();

        if (Objects.isNull(bapId) || Objects.isNull(messageId) || Objects.isNull(transactionId)) {
            log.error("Missing required fields so unable to send issue request: BAP ID: {}, Message ID: {}, Transaction ID: {}", bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String signatureHeader;

        try {
            String value=objectMapper.writeValueAsString(issuePayload);
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for issue request with Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);
        }
        catch (Exception e) {
            log.error("Error while  generating signature header Message ID: {}, TransactionEntity ID: {}, Error: {}",
                    messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);

        HttpEntity<IssueRequest> requestEntity = new HttpEntity<>(issuePayload, headers);

        try {
            log.info("Sending issue request to URL: {}, Message ID: {}, TransactionEntity ID: {}",
                    url, messageId, transactionId);
            ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);
            log.info("Received response for On issue Request");

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(issuePayload.getContext().getAction())
                    .transactionId(issuePayload.getContext().getTransaction_id())
                    .messageId(issuePayload.getContext().getMessage_id())
                    .bapId(issuePayload.getContext().getBap_id())
                    .bapUri(issuePayload.getContext().getBap_uri())
                    .issueId(issuePayload.getMessage().getIssue().getId())
                    .build();

            return ResponseEntity.ok(responseDTO);

        }
        catch (Exception e) {
            log.error("An error occurred while sending issue request: {}, Message ID: {}, TransactionEntity ID: {}",
                    e.getMessage(), messageId, transactionId, e);
            throw e;
        }

    }

    public ResponseEntity<?> issueStatus(IssueStatusRequest issueStatus) {
        IssueStatusRequest.Context context = issueStatus.getContext();
        if (Objects.isNull(context)) {
            log.error("issueStatus context is null. Unable to proceed.");
            throw new IllegalArgumentException("issueRequest context cannot be null.");
        }

        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId= context.getTransaction_id();
        String url=issueStatus.getContext().getBpp_uri()+issueStatus.getContext().getAction();

        if (Objects.isNull(bapId) || Objects.isNull(messageId) || Objects.isNull(transactionId)) {
            log.error("Missing required fields so unable to send issue status request: BAP ID: {}, Message ID: {}, Transaction ID: {}", bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String signatureHeader;

        try {
            String value=objectMapper.writeValueAsString(issueStatus);
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for issue status request with Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);
        }
        catch (Exception e) {
            log.error("Error while  generating signature header Message ID: {}, TransactionEntity ID: {}, Error: {}",
                    messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);

        HttpEntity<IssueStatusRequest> requestEntity = new HttpEntity<>(issueStatus, headers);

        try {
            log.info("Sending issueStatus request to URL: {}, Message ID: {}, TransactionEntity ID: {}",
                    url, messageId, transactionId);
            ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);
            log.info("Received response for On issueStatus Request");

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(issueStatus.getContext().getAction())
                    .transactionId(issueStatus.getContext().getTransaction_id())
                    .messageId(issueStatus.getContext().getMessage_id())
                    .bapId(issueStatus.getContext().getBap_id())
                    .bapUri(issueStatus.getContext().getBap_uri())
                    .issueId(issueStatus.getMessage().getIssue_id())
                    .build();

            return ResponseEntity.ok(responseDTO);

        }
        catch (Exception e) {
            log.error("An error occurred while sending issueStatus request: {}, Message ID: {}, TransactionEntity ID: {}",
                    e.getMessage(), messageId, transactionId, e);
            throw e;
        }

    }

    public ResponseEntity<?> issueClosure(IssueCloseRequest issueClosureRequest) {

        IssueCloseRequest.Context context = issueClosureRequest.getContext();
        if (Objects.isNull(context)) {
            log.error("Issue Closure context is null. Unable to proceed.");
            throw new IllegalArgumentException("issueRequest context cannot be null.");
        }

        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId= context.getTransaction_id();
        String url=issueClosureRequest.getContext().getBpp_uri()+"/issue";

        if (Objects.isNull(bapId) || Objects.isNull(messageId) || Objects.isNull(transactionId)) {
            log.error("Missing required fields so unable to send issue closure request: BAP ID: {}, Message ID: {}, Transaction ID: {}", bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String signatureHeader;

        try {
            String value=objectMapper.writeValueAsString(issueClosureRequest);
            signatureHeader = Routes.generateSignatureHeader(value, privateKey, subscriberId, uniqueKeyId);
            log.info("SignatureHeader created successfully for issue closure request with Message ID: {}, TransactionEntity ID: {}", messageId, transactionId);
        }
        catch (Exception e) {
            log.error("Error while  generating signature header Message ID: {}, TransactionEntity ID: {}, Error: {}",
                    messageId, transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);

        HttpEntity<IssueCloseRequest> requestEntity = new HttpEntity<>(issueClosureRequest, headers);

        try {
            log.info("Sending issue closure request to URL: {}, Message ID: {}, TransactionEntity ID: {}",
                    url, messageId, transactionId);
           ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Acknowledgement.class);

            ResponseDTO responseDTO=ResponseDTO.builder()
                    .action(issueClosureRequest.getContext().getAction())
                    .transactionId(issueClosureRequest.getContext().getTransaction_id())
                    .messageId(issueClosureRequest.getContext().getMessage_id())
                    .bapId(issueClosureRequest.getContext().getBap_id())
                    .bapUri(issueClosureRequest.getContext().getBap_uri())
                    .issueId(issueClosureRequest.getMessage().getIssue().getId())
                    .build();
            return ResponseEntity.ok(responseEntity.getBody());

        }
        catch (Exception e) {
            log.error("An error occurred while sending issue closure request: {}, Message ID: {}, TransactionEntity ID: {}",
                    e.getMessage(), messageId, transactionId, e);
            throw e;
        }

    }
    public ResponseEntity<?> sendMotorFirstInitRequest(MotorInitRequest initRequest) {

        MotorInitRequest.Context context = initRequest.getContext();

        if (Objects.isNull(context)) {
            log.error("MotorInitRequest context is null. Unable to proceed.");
            throw new IllegalArgumentException("MotorInitRequest context cannot be null.");
        }

        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId = context.getTransaction_id();
        String url = context.getBpp_uri() + context.getAction();

        if (StringUtils.isEmpty(bapId) || StringUtils.isEmpty(messageId) || StringUtils.isEmpty(transactionId)) {
            log.error("Missing required fields for motor init: BAP ID: {}, Message ID: {}, Transaction ID: {}",
                    bapId, messageId, transactionId);
            throw new IllegalArgumentException("BAP ID, Message ID, and Transaction ID must be provided.");
        }

        String value;
        try {
            value = objectMapper.writeValueAsString(initRequest);
        } catch (JsonProcessingException e) {
            log.error("Error serializing motor init request", e);
            throw new RuntimeException("Error serializing motor init request", e);
        }

        String signatureHeader;
        try {
            signatureHeader = Routes.generateSignatureHeader(
                    value, privateKey, subscriberId, uniqueKeyId);
        } catch (Exception e) {
            log.error("Error generating signature header for motor init", e);
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);

        HttpEntity<MotorInitRequest> requestEntity =
                new HttpEntity<>(initRequest, headers);

        try {
            log.info("Sending Motor Init request to URL: {}, Message ID: {}, Transaction ID: {}",
                    url, messageId, transactionId);

            ResponseEntity<?> responseEntity = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, Acknowledgement.class);

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .action(context.getAction())
                    .transactionId(transactionId)
                    .messageId(messageId)
                    .bapId(bapId)
                    .bapUri(context.getBap_uri())
                    .build();

            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            log.error("Error while sending Motor Init request", e);
            throw e;
        }
    }



    public ResponseEntity<?> sendUpdate(MotorUpdateRequest updateRequest) {

        MotorUpdateRequest.Context context = updateRequest.getContext();

        if (Objects.isNull(context)) {
            log.error("Update Request context is null. Unable to proceed.");
            throw new IllegalArgumentException("Update Request context cannot be null.");
        }

        String bapId = context.getBap_id();
        String messageId = context.getMessage_id();
        String transactionId = context.getTransaction_id();
        String url = context.getBpp_uri() + context.getAction(); // /update

        if (Objects.isNull(bapId) || Objects.isNull(messageId) || Objects.isNull(transactionId)) {
            log.error(
                    "Missing required fields so unable to send update request: BAP ID: {}, Message ID: {}, Transaction ID: {}",
                    bapId, messageId, transactionId
            );
            throw new IllegalArgumentException(
                    "BAP ID, Message ID, and Transaction ID must be provided."
            );
        }

        String signatureHeader;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(updateRequest);

            signatureHeader = Routes.generateSignatureHeader(
                    value, privateKey, subscriberId, uniqueKeyId
            );

            log.info(
                    "SignatureHeader created successfully for update request | msgId={} | txnId={}",
                    messageId, transactionId
            );

        } catch (Exception e) {
            log.error(
                    "Error while generating signature header for update | msgId={} | txnId={} | error={}",
                    messageId, transactionId, e.getMessage(), e
            );
            throw new RuntimeException("Failed to generate signature header", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", signatureHeader);

        HttpEntity<MotorUpdateRequest> requestEntity =
                new HttpEntity<>(updateRequest, headers);

        try {
            log.info(
                    "Sending UPDATE request to URL: {} | msgId={} | txnId={}",
                    url, messageId, transactionId
            );

            ResponseEntity<?> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Acknowledgement.class
            );

            log.info(
                    "Received response for update | msgId={} | txnId={} | response={}",
                    messageId, transactionId, responseEntity.getBody()
            );

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .action(context.getAction())
                    .transactionId(context.getTransaction_id())
                    .messageId(context.getMessage_id())
                    .bapId(context.getBap_id())
                    .bapUri(context.getBap_uri())
                    .build();

            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            log.error(
                    "An error occurred while processing update request | msgId={} | txnId={} | error={}",
                    messageId, transactionId, e.getMessage(), e
            );
            throw e;
        }
    }



}
