package com.can.buyerApp.service;

import com.can.buyerApp.dto.ProviderDTO;
import com.can.buyerApp.dto.SearchCatalogDTO;
import com.can.buyerApp.request.MotorOnSearchRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import com.can.buyerApp.dto.SearchQuoteResponse;

public interface MotorOnSearchService {

    /**
     * Saves motor on_search request received from BPP
     * @param request MotorOnSearchRequest object
     * @return ResponseEntity with acknowledgement
     */
    ResponseEntity<?> saveMotorOnSearchRequest(MotorOnSearchRequest request);

    /**
     * Retrieves motor insurance categories by transaction ID
     * @param transactionId Transaction ID
     * @param providerId Provider ID
     * @param messageId Message ID
     * @return ResponseEntity with insurance categories
     */
    ResponseEntity<?> getMotorInsuranceCategoryByTransactionId(String transactionId,
                                                               String providerId,
                                                               String messageId);

    /**
     * Retrieves motor insurance categories by vehicle type
     * @param transactionId Transaction ID
     * @param messageId Message ID
     * @param vehicleType Vehicle type (TWO_WHEELER or FOUR_WHEELER)
     * @return ResponseEntity with insurance categories
     */
    ResponseEntity<?> getMotorInsuranceCategoriesByVehicleType(String transactionId,
                                                               String messageId,
                                                               String vehicleType);

    /**
     * Get available vehicle types for a transaction
     * @param transactionId Transaction ID
     * @return List of vehicle types
     */
    List<String> getAvailableVehicleTypes(String transactionId);

    /**
     * Retrieves motor insurance catalogs
     * @param transactionId Transaction ID
     * @param messageId Message ID
     * @return List of SearchCatalogDTO
     */
    List<SearchCatalogDTO> getMotorCatalogs(String transactionId, String messageId);

    /**
     * Retrieves list of motor insurance providers
     * @param transactionId Transaction ID
     * @param messageId Message ID
     * @return List of ProviderDTO
     */
    List<ProviderDTO> getMotorProviderList(String transactionId, String messageId);

    /**
     * Retrieves motor insurance form URL and details
     * @param transactionId Transaction ID
     * @param providerId Provider ID
     * @param formUrl Form URL
     * @param messageId Message ID
     * @param formId Form ID
     * @return Map containing form HTML and submit URL
     */
    Map<String, String> getMotorForm(String transactionId, String providerId,
                                     String formUrl, String messageId, String formId, String categoryId);

    List<SearchQuoteResponse> getQuotesByTransactionId(String transactionId);
}