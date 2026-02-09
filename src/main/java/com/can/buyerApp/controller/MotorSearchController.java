package com.can.buyerApp.controller;

import com.can.buyerApp.constants.PreConstants;
import com.can.buyerApp.dto.ProviderDTO;
import com.can.buyerApp.dto.SearchCatalogDTO;
import com.can.buyerApp.dto.SearchQuoteResponse;
import com.can.buyerApp.request.MotorOnSearchRequest;
import com.can.buyerApp.service.MotorOnSearchService;
import com.can.buyerApp.service.MotorSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class MotorSearchController {

    private final MotorSearchService motorSearchService;
    private final MotorOnSearchService motorOnSearchService;

    public MotorSearchController(MotorSearchService motorSearchService,
                                 MotorOnSearchService motorOnSearchService) {
        this.motorSearchService = motorSearchService;
        this.motorOnSearchService = motorOnSearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchRequest(@RequestParam String domain,
                                           @RequestParam String type,
                                           @RequestParam(required = false) String transactionId,
                                           @RequestParam(required = false) String messageId,
                                           @RequestParam(required = false) String submissionId,
                                           @RequestParam(required = false) String providerId,
                                           @RequestParam(required = false) String formStatus,
                                           @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) String formId,
                                           @RequestParam(required = false) String agentId,
                                           @RequestParam(required = false) String categoryId) {
        try {
            // Validate domain
            if (!PreConstants.VALID_DOMAIN.equals(domain)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid domain. Expected: " + PreConstants.VALID_DOMAIN);
            }

            // Validate type
            if (!PreConstants.MOTOR_INSURANCE.equals(type)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid type. Expected: " + PreConstants.MOTOR_INSURANCE);
            }

            // Check if this is a second search request
            if (StringUtils.isNotBlank(transactionId) &&
                    StringUtils.isNotBlank(messageId) &&
                    StringUtils.isNotBlank(submissionId) &&
                    StringUtils.isNotBlank(providerId) &&
                    StringUtils.isNotBlank(categoryId) &&
                    StringUtils.isNotBlank(formId)) {

                log.info("Valid parameters for second motor search. Processing second search request.");

                return motorSearchService.sendSecondMotorSearchRequest(
                        domain, type, transactionId, messageId, submissionId,
                        providerId, formStatus, formId, categoryId);
            }
            // First search request
            else if (userId != null) {
                log.info("Valid parameters for first motor search. Processing first search request.");
                return motorSearchService.sendMotorSearchRequest(domain, type, userId, agentId);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid parameters. For first search, userId is required. " +
                                "For second search, transactionId, messageId, submissionId, providerId, formId, bppId, and bppUri are required.");
            }
        } catch (Exception e) {
            log.error("Error occurred while processing the motor search request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the motor search request. Please try again later.");
        }
    }

    @PostMapping("/on_search")
    public ResponseEntity<?> createOnSearchRequest(@RequestBody MotorOnSearchRequest onSearchRequest) {
        if (Objects.isNull(onSearchRequest)) {
            log.warn("Received invalid MotorOnSearchRequest: {}", onSearchRequest);
            return ResponseEntity.badRequest().body("Invalid MotorOnSearchRequest.");
        }

        try {
            log.info("Processing motor on_search request");
            return ResponseEntity.ok(motorOnSearchService.saveMotorOnSearchRequest(onSearchRequest));
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing MotorOnSearchRequest: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the MotorOnSearchRequest.");
        }
    }

    @GetMapping("/search-transaction")
    public ResponseEntity<?> getMotorInsuranceCategoryByTransactionId(
            @RequestParam String transactionId,
            @RequestParam String providerId,
            @RequestParam String messageId) {
        try {
            return motorOnSearchService.getMotorInsuranceCategoryByTransactionId(
                    transactionId, providerId, messageId);
        } catch (Exception e) {
            log.error("Error retrieving motor insurance categories: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving motor insurance categories.");
        }
    }

    @GetMapping("/vehicle-types")
    public ResponseEntity<List<String>> getAvailableVehicleTypes(@RequestParam String transactionId) {
        try {
            List<String> vehicleTypes = motorOnSearchService.getAvailableVehicleTypes(transactionId);
            return ResponseEntity.ok(vehicleTypes);
        } catch (Exception e) {
            log.error("Error retrieving vehicle types: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/categories-by-vehicle")
    public ResponseEntity<?> getMotorCategoriesByVehicleType(
            @RequestParam String transactionId,
            @RequestParam String messageId,
            @RequestParam String vehicleType) {
        try {
            return motorOnSearchService.getMotorInsuranceCategoriesByVehicleType(
                    transactionId, messageId, vehicleType);
        } catch (Exception e) {
            log.error("Error retrieving categories by vehicle type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving categories by vehicle type.");
        }
    }

    @GetMapping("/search-catalog")
    public ResponseEntity<List<SearchCatalogDTO>> getMotorCatalog(
            @RequestParam String transactionId,
            @RequestParam String messageId) {
        try {
            List<SearchCatalogDTO> catalogs = motorOnSearchService.getMotorCatalogs(transactionId, messageId);
            return ResponseEntity.ok(catalogs);
        } catch (Exception e) {
            log.error("Error retrieving motor catalogs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/providers-list")
    public ResponseEntity<List<ProviderDTO>> getMotorProviders(
            @RequestParam String transactionId,
            @RequestParam String messageId) {
        try {
            List<ProviderDTO> providers = motorOnSearchService.getMotorProviderList(transactionId, messageId);
            return ResponseEntity.ok(providers);
        } catch (Exception e) {
            log.error("Error retrieving motor providers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/form-url")
    public ResponseEntity<Map<String, String>> getMotorForm(
            @RequestParam String transactionId,
            @RequestParam String providerId,
            @RequestParam String formUrl,
            @RequestParam String messageId,
            @RequestParam String formId,
            @RequestParam String categoryId
    ) {
        try {
            Map<String, String> formData = motorOnSearchService.getMotorForm(
                    transactionId, providerId, formUrl, messageId, formId, categoryId);
            return ResponseEntity.ok(formData);
        } catch (Exception e) {
            log.error("Error retrieving motor form: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/motor-insurance-quotes")
    public ResponseEntity<List<SearchQuoteResponse>> getMotorInsuranceQuotes(
            @RequestParam String transactionId) {

        List<SearchQuoteResponse> response =
                motorOnSearchService.getQuotesByTransactionId(transactionId);

        return ResponseEntity.ok(response);
    }


}