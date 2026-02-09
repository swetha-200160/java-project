//package com.can.buyerApp.service.impl;
//
//import org.springframework.http.HttpHeaders;  
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import com.can.buyerApp.entity.MotorNomineeForm;
//import com.can.buyerApp.entity.MotorVehicleForm;
//import com.can.buyerApp.repository.MotorNomineeFormRepository;
//import com.can.buyerApp.repository.MotorVehicleFormRepository;
//import com.can.buyerApp.service.MotorFormService;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//
//@Slf4j
//@Service
//public class MotorFormServiceImpl implements MotorFormService {
//
//    private final MotorVehicleFormRepository motorVehicleFormRepository;
//    private final MotorNomineeFormRepository motorNomineeFormRepository;
//
//    public MotorFormServiceImpl(MotorVehicleFormRepository motorVehicleFormRepository,
//                                MotorNomineeFormRepository motorNomineeFormRepository) {
//        this.motorVehicleFormRepository = motorVehicleFormRepository;
//        this.motorNomineeFormRepository = motorNomineeFormRepository;
//    }
//
//    @Override
//    public ResponseEntity<?> submitMotorVehicleForm(Map<String, String> formData, 
//                                                     String formUrl, 
//                                                     String transactionId, 
//                                                     String messageId) {
//        try {
//            log.info("Submitting motor vehicle form for transaction: {}", transactionId);
//            
//            // Submit form to BPP
//            ResponseEntity<String> responseBody = submitFormToBPP(formData, formUrl);
//            String response = responseBody.getBody();
//            
//            log.info("Motor vehicle form submitted to BPP successfully. Response: {}", response);
//            
//            if (response != null) {
//                MotorVehicleForm vehicleForm = new MotorVehicleForm();
//                
//                // Parse response to get submission_id
//                ObjectMapper objectMapper = new ObjectMapper();
//                JsonNode jsonResponse = objectMapper.readTree(response);
//                String submissionId = jsonResponse.get("submission_id").asText();
//                
//                // Set Vehicle Details - MATCH YOUR ENTITY FIELDS
//                vehicleForm.setVehicleType(formData.get("vehicleType"));
//                vehicleForm.setRegistrationNumber(formData.get("registrationNumber"));
//                vehicleForm.setMake(formData.get("make"));
//                vehicleForm.setModel(formData.get("model"));
//                vehicleForm.setVariant(formData.get("variant"));
//                vehicleForm.setFuelType(formData.get("fuelType"));
//                vehicleForm.setManufactureYear(formData.get("manufactureYear"));
//                vehicleForm.setEngineNumber(formData.get("engineNumber"));
//                vehicleForm.setChassisNumber(formData.get("chassisNumber"));
//                vehicleForm.setCubicCapacity(formData.get("cubicCapacity"));
//                vehicleForm.setSeatingCapacity(formData.get("seatingCapacity"));
//                vehicleForm.setPolicyType(formData.get("policyType"));
//                vehicleForm.setPreviousPolicyNumber(formData.get("previousPolicyNumber"));
//                vehicleForm.setPreviousInsurer(formData.get("previousInsurer"));
//                vehicleForm.setClaimHistory(formData.get("claimHistory"));
//                vehicleForm.setOwnerType(formData.get("ownerType"));
//                
//                // Customer Details
//                vehicleForm.setFirstName(formData.get("firstName"));
//                vehicleForm.setLastName(formData.get("lastName"));
//                vehicleForm.setCustomerEmail(formData.get("customerEmail"));
//                vehicleForm.setCustomerPhone(formData.get("customerPhone"));
//                
//                // Metadata
//                vehicleForm.setTransactionId(transactionId);
//                vehicleForm.setMessageId(messageId);
//                vehicleForm.setSubmissionId(submissionId);
//                vehicleForm.setCreatedAt(LocalDateTime.now());
//                vehicleForm.setUpdatedAt(LocalDateTime.now());
//                
//                // Save to database
//                motorVehicleFormRepository.save(vehicleForm);
//                log.info("Motor vehicle form saved successfully to database. Transaction: {}, Submission ID: {}", 
//                         transactionId, submissionId);
//            }
//            
//            return ResponseEntity.ok().body(response);
//            
//        } catch (Exception e) {
//            log.error("Error saving motor vehicle form for transaction: {}. Error: {}", 
//                      transactionId, e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error saving motor vehicle form: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public ResponseEntity<?> submitMotorNomineeForm(Map<String, String> formData, 
//                                                     String formUrl, 
//                                                     String transactionId, 
//                                                     String messageId) {
//        try {
//            log.info("Submitting motor nominee form for transaction: {}", transactionId);
//            
//            // Submit form to BPP
//            ResponseEntity<String> responseBody = submitFormToBPP(formData, formUrl);
//            String response = responseBody.getBody();
//            
//            log.info("Motor nominee form submitted to BPP successfully. Response: {}", response);
//            
//            if (response != null) {
//                MotorNomineeForm nomineeForm = new MotorNomineeForm();
//                
//                // Parse response to get submission_id
//                ObjectMapper objectMapper = new ObjectMapper();
//                JsonNode jsonResponse = objectMapper.readTree(response);
//                String submissionId = jsonResponse.get("submission_id").asText();
//                
////                // Set Nominee Details
////                nomineeForm.setNomineeName(formData.get("nomineeName"));
////                nomineeForm.setNomineeDOB(formData.get("nomineeDOB"));
////                nomineeForm.setRelationshipNominee(formData.get("relationshipNominee"));
////                nomineeForm.setAppointeeName(formData.get("appointeeName"));
////                nomineeForm.setAppointeeRelationship(formData.get("appointeeRelationship"));
////                
////                // Metadata
////                nomineeForm.setFormId(formData.get("formId"));
////                nomineeForm.setTransactionId(transactionId);
////                nomineeForm.setMessageId(messageId);
////                nomineeForm.setSubmissionId(submissionId);
////                nomineeForm.setCreatedAt(LocalDateTime.now());
////                nomineeForm.setUpdatedAt(LocalDateTime.now());
//                
//                // Save to database
//                motorNomineeFormRepository.save(nomineeForm);
//                log.info("Motor nominee form saved successfully to database. Transaction: {}, Submission ID: {}", 
//                         transactionId, submissionId);
//            }
//            
//            return ResponseEntity.ok().body(response);
//            
//        } catch (Exception e) {
//            log.error("Error saving motor nominee form for transaction: {}. Error: {}", 
//                      transactionId, e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error saving motor nominee form: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Helper method to submit form data to BPP
//     * Sends form data as application/x-www-form-urlencoded
//     */
//    private ResponseEntity<String> submitFormToBPP(Map<String, String> formData, String formUrl) {
//        RestTemplate restTemplate = new RestTemplate();
//        try {
//            // Set headers for form submission
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            
//            // Build form body from map
//            StringBuilder formBody = new StringBuilder();
//            for (Map.Entry<String, String> entry : formData.entrySet()) {
//                if (formBody.length() > 0) {
//                    formBody.append("&");
//                }
//                formBody.append(entry.getKey()).append("=").append(entry.getValue());
//            }
//            
//            // Create HTTP request
//            HttpEntity<String> requestEntity = new HttpEntity<>(formBody.toString(), headers);
//            
//            // Submit to BPP
//            ResponseEntity<String> response = restTemplate.exchange(
//                    formUrl, 
//                    HttpMethod.POST, 
//                    requestEntity, 
//                    String.class
//            );
//            
//            log.info("Form submitted to BPP successfully. Status: {}", response.getStatusCode());
//            return response;
//            
//        } catch (Exception e) {
//            log.error("Error submitting form to BPP at URL: {}. Error: {}", formUrl, e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error submitting form to BPP: " + e.getMessage());
//        }
//    }
//}
