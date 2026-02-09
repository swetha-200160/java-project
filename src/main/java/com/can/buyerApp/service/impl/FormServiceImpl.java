package com.can.buyerApp.service.impl;

import com.can.buyerApp.entity.*;
import com.can.buyerApp.repository.*;
import com.can.buyerApp.service.FormService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class FormServiceImpl implements FormService {

    private final MotorVehicleFormRepository motorVehicleFormRepository;
    private final MotorManualReviewFormRepository motorManualReviewFormRepository;
    private final PanDobFormRepository panDobFormRepository;
    private final  VehicleInformationFormRepository vehicleInformationFormRepository;
    private final PersonalDetailsFormRepository personalDetailsFormRepository;

    private final IndividualFormRepository formRepository;
    private final ProposerFormRepository proposerFormRepository;
    private final NomineeFormRepository nomineeFormRepository;
    private final FamilyFormRepository familyFormRepository;

    public FormServiceImpl(MotorVehicleFormRepository motorVehicleFormRepository, MotorManualReviewFormRepository motorManualReviewFormRepository, PanDobFormRepository panDobFormRepository, VehicleInformationFormRepository vehicleInformationFormRepository, PersonalDetailsFormRepository personalDetailsFormRepository, IndividualFormRepository formRepository, ProposerFormRepository proposerFormRepository, NomineeFormRepository nomineeFormRepository, FamilyFormRepository familyFormRepository) {
        this.motorVehicleFormRepository = motorVehicleFormRepository;
        this.motorManualReviewFormRepository = motorManualReviewFormRepository;
        this.panDobFormRepository = panDobFormRepository;
        this.vehicleInformationFormRepository = vehicleInformationFormRepository;
        this.personalDetailsFormRepository = personalDetailsFormRepository;
        this.formRepository = formRepository;
        this.proposerFormRepository = proposerFormRepository;
        this.nomineeFormRepository = nomineeFormRepository;
        this.familyFormRepository = familyFormRepository;
    }

    @Override
//    public ResponseEntity<String> submitMotorVehicleForm(
//            Map<String, String> formData,
//            String formUrl,
//            String transactionId,
//            String messageId) {
//        try {
//            ResponseEntity<String> responseBody = submitForm(formData, formUrl);
//            String response = responseBody.getBody();
//            log.info("Motor vehicle form submitted successfully. Response: {}", response);
//
//            if (response != null) {
//                ObjectMapper mapper = new ObjectMapper();
//                JsonNode jsonResponse = mapper.readTree(response);
//                String submissionId = jsonResponse.get("submission_id").asText();
//
//                MotorVehicleForm vehicle = new MotorVehicleForm();
//
//                // Personal Information
//                vehicle.setFirstName(formData.get("firstName"));
//                vehicle.setLastName(formData.get("lastName"));
//                vehicle.setEmail(formData.get("email"));
//                vehicle.setPhone(formData.get("phone"));
//                vehicle.setGender(formData.get("gender"));
//
//                // Vehicle Basic Information
//                vehicle.setVehicleType(formData.get("vehicleType"));
//                vehicle.setRegistrationNumber(formData.get("registrationNumber"));
//                vehicle.setVehicleUniqueCode(formData.get("vehicleUniqueCode"));
//                vehicle.setRtoCode(formData.get("rtoCode"));
//                vehicle.setRegistrationDate(parseDate(formData.get("registrationDate")));
//                vehicle.setMake(formData.get("make"));
//                vehicle.setModel(formData.get("model"));
//                vehicle.setVariant(formData.get("variant"));
//                vehicle.setFuelType(formData.get("fuelType"));
//                vehicle.setManufactureYear(formData.get("manufactureYear"));
//                vehicle.setEngineNumber(formData.get("engineNumber"));
//                vehicle.setChassisNumber(formData.get("chassisNumber"));
//                vehicle.setCubicCapacity(formData.get("cubicCapacity"));
//                vehicle.setSeatingCapacity(formData.get("seatingCapacity"));
//
//                // Policy Information
//                vehicle.setPolicyType(formData.get("policyType"));
//                vehicle.setCoverType(formData.get("coverType"));
//                vehicle.setIdv(formData.get("idv"));
//                vehicle.setPersonalAccidentCover(formData.get("PersonalAccidentCover"));
//                vehicle.setPaTenure(formData.get("paTenure"));
//                vehicle.setPolicyTenure(formData.get("policyTenure"));
//                vehicle.setNcb(formData.get("ncb"));
//
//                // Previous Policy Information
//                vehicle.setPreviousPolicyNumber(formData.get("previousPolicyNumber"));
//                vehicle.setPreviousPolicyInsurerName(formData.get("previousPolicyInsurerName"));
//                vehicle.setPreviousInsurer(formData.get("previousInsurer"));
//                vehicle.setPreviousPolicyType(formData.get("previousPolicyType"));
//                vehicle.setPreviousPolicyDate(parseDate(formData.get("previousPolicyDate")));
//                vehicle.setPreviousPolicyCustomerName(formData.get("previousPolicyCustomerName"));
//                vehicle.setClaimHistory(formData.get("claimHistory"));
//                vehicle.setClaimStatus(parseBoolean(formData.get("claimStatus")));
//
//                // Ownership
//                vehicle.setOwnerType(formData.get("ownerType"));
//
//                // Submission metadata
//                vehicle.setSubmissionId(submissionId);
//                vehicle.setTransactionId(transactionId);
//                vehicle.setMessageId(messageId);
//                vehicle.setFormId(formData.get("formId"));
//                vehicle.setCreatedAt(LocalDateTime.now());
//                vehicle.setUpdatedAt(LocalDateTime.now());
//
//                motorVehicleFormRepository.save(vehicle);
//            }
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error saving motor vehicle form", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error saving motor vehicle form: " + e.getMessage());
//        }
//    }

    public ResponseEntity<String> submitMotorVehicleForm(
            Map<String, String> formData,
            String formUrl,
            String transactionId,
            String messageId) {

        try {
            ResponseEntity<String> responseEntity = submitForm(formData, formUrl);

            HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
            String responseBody = responseEntity.getBody();

            // Handle non-2xx first
            if (!status.is2xxSuccessful()) {
                log.error("Motor vehicle form submission failed. Status={}, Body={}",
                        status, responseBody);
                return ResponseEntity.status(status)
                        .body("Form submission failed: " + responseBody);
            }

            // Empty body check
            if (responseBody == null || responseBody.isBlank()) {
                log.error("Empty response from seller");
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Empty response from seller");
            }

            // Content-Type validation (optional but recommended)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse;
            try {
                jsonResponse = mapper.readTree(responseBody);
            } catch (Exception ex) {
                log.error("Non-JSON response received: {}", responseBody);
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Invalid response from seller");
            }

            // Mandatory field check
            if (!jsonResponse.hasNonNull("submission_id")) {
                log.error("submission_id missing in response: {}", responseBody);
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Invalid seller response");
            }

            String submissionId = jsonResponse.get("submission_id").asText();

            // Build entity ONLY after success confirmed
            MotorVehicleForm vehicle = new MotorVehicleForm();

            vehicle.setFirstName(formData.get("firstName"));
            vehicle.setLastName(formData.get("lastName"));
            vehicle.setEmail(formData.get("email"));
            vehicle.setPhone(formData.get("phone"));
            vehicle.setGender(formData.get("gender"));

            vehicle.setVehicleType(formData.get("vehicleType"));
            vehicle.setRegistrationNumber(formData.get("registrationNumber"));
            vehicle.setVehicleUniqueCode(formData.get("vehicleUniqueCode"));
            vehicle.setRtoCode(formData.get("rtoCode"));
            vehicle.setRegistrationDate(parseDate(formData.get("registrationDate")));

            vehicle.setPolicyType(formData.get("policyType"));
            vehicle.setCoverType(formData.get("coverType"));
            vehicle.setIdv(formData.get("idv"));
            vehicle.setPersonalAccidentCover(formData.get("PersonalAccidentCover"));
            vehicle.setPaTenure(formData.get("paTenure"));
            vehicle.setPolicyTenure(formData.get("policyTenure"));
            vehicle.setNcb(formData.get("ncb"));

            vehicle.setSubmissionId(submissionId);
            vehicle.setTransactionId(transactionId);
            vehicle.setMessageId(messageId);
            vehicle.setFormId(formData.get("formId"));
            vehicle.setCreatedAt(LocalDateTime.now());
            vehicle.setUpdatedAt(LocalDateTime.now());

            motorVehicleFormRepository.save(vehicle);

            log.info("Motor vehicle form saved successfully. submissionId={}", submissionId);
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            log.error("Error saving motor vehicle form", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving motor vehicle form");
        }
    }


    // Helper methods
    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString);
        } catch (Exception e) {
            log.warn("Failed to parse date: {}", dateString, e);
            return null;
        }
    }

    private Boolean parseBoolean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return "on".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
    }

    @Override
    public ResponseEntity<?> submitMotorManualReviewForm(
            Map<String, String> formData,
            String formUrl,
            String transactionId,
            String messageId) {

        try {
            ResponseEntity<String> responseBody = submitForm(formData, formUrl);
            String response = responseBody.getBody();

            if (response != null) {

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonResponse = mapper.readTree(response);
                String submissionId = jsonResponse.get("submission_id").asText();

                // HTML form values
                String name = formData.get("name");
                String email = formData.get("email");
                String message = formData.get("message");

                MotorManualReviewForm review = new MotorManualReviewForm();

                review.setName(name);
                review.setEmail(email);
                review.setRemarks(message);

                review.setSubmissionId(submissionId);
                review.setTransactionId(transactionId);
                review.setMessageId(messageId);
                review.setCreatedAt(LocalDateTime.now());
                review.setUpdatedAt(LocalDateTime.now());

                motorManualReviewFormRepository.save(review);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving manual review form: " + e.getMessage());
        }
    }
//    @Override
//    public ResponseEntity<?> submitMotorManualReviewForm(
//            Map<String, String> formData,
//            String formUrl,
//            String transactionId,
//            String messageId) {
//
//        try {
//            ResponseEntity<String> responseBody = submitForm(formData, formUrl);
//            String response = responseBody.getBody();
//
//            log.info("Motor manual review form submitted. Response={}", response);
//
//            if (response != null) {
//
//                ObjectMapper mapper = new ObjectMapper();
//                JsonNode jsonResponse = mapper.readTree(response);
//                String submissionId = jsonResponse.get("submission_id").asText();
//
//                MotorManualReviewForm review = new MotorManualReviewForm();
//
//                review.setReviewReason(formData.get("reviewReason"));
//                review.setInspectionRequired(formData.get("inspectionRequired"));
//                review.setInspectionDate(formData.get("inspectionDate"));
//                review.setInspectionLocation(formData.get("inspectionLocation"));
//                review.setOdometerReading(formData.get("odometerReading"));
//                review.setRcAvailable(formData.get("rcAvailable"));
//                review.setHypothecation(formData.get("hypothecation"));
//                review.setPreviousClaimDetails(formData.get("previousClaimDetails"));
//                review.setRemarks(formData.get("remarks"));
//                review.setAdditionalNotes(formData.get("additionalNotes"));
//
//                review.setSubmissionId(submissionId);
//                review.setTransactionId(transactionId);
//                review.setMessageId(messageId);
//                review.setCreatedAt(LocalDateTime.now());
//                review.setUpdatedAt(LocalDateTime.now());
//
//                motorManualReviewFormRepository.save(review);
//            }
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error saving motor manual review form", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error saving motor manual review form: " + e.getMessage());
//        }
//    }

    @Override
    public ResponseEntity<?> submitPanDobForm(
            Map<String, String> formData,
            String formUrl,
            String transactionId,
            String messageId) {

        try {
            ResponseEntity<String> responseBody = submitForm(formData, formUrl);
            String response = responseBody.getBody();

            log.info("PAN-DOB form submitted successfully. Response={}", response);

            if (response != null) {

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonResponse = mapper.readTree(response);
                String submissionId = jsonResponse.get("submission_id").asText();

                PanDobForm panDobForm = new PanDobForm();
                panDobForm.setDob(formData.get("dob"));
                panDobForm.setPanValue(formData.get("panValue"));
                panDobForm.setFormId(formData.get("formId"));

                panDobForm.setSubmissionId(submissionId);
                panDobForm.setTransactionId(transactionId);
                panDobForm.setMessageId(messageId);
                panDobForm.setCreatedAt(LocalDateTime.now());
                panDobForm.setUpdatedAt(LocalDateTime.now());

                panDobFormRepository.save(panDobForm);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error saving PAN-DOB form", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving PAN-DOB form: " + e.getMessage());
        }
    }


    @Override
    public ResponseEntity<?> submitVehicleInformationForm(
            Map<String, String> formData,
            String formUrl,
            String transactionId,
            String messageId) {

        String formId = formData.get("formId");

        try {
            log.info(
                    "Submitting Vehicle Information form | txnId={} | formId={}",
                    transactionId,
                    formId
            );

            // ================= SUBMIT TO BPP =================
            ResponseEntity<String> responseBody =
                    submitForm(formData, formUrl);

            String response = responseBody.getBody();

            log.info(
                    "Vehicle Information form submitted | txnId={} | response={}",
                    transactionId,
                    response
            );

            // ================= PARSE SUBMISSION ID =================
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response);
            String submissionId = jsonNode.get("submission_id").asText();

            // ================= SAVE TO DB =================
            VehicleInformationForm entity = new VehicleInformationForm();

            entity.setTransactionId(transactionId);
            entity.setMessageId(messageId);
            entity.setFormId(formId);
            entity.setSubmissionId(submissionId);

            // -------- Vehicle Details --------
            entity.setRegistrationNumber(formData.get("registrationNumber"));
            entity.setChassisNumber(formData.get("chassisNumber"));
            entity.setEngineNumber(formData.get("engineNumber"));

            // -------- Previous Policy Details --------
            entity.setPreviousPolicyNumber(formData.get("previousPolicyNumber"));
            entity.setPreviousPolicyIssuer(formData.get("previousPolicyIssuer"));
            entity.setPreviousTPPolicyIssuer(formData.get("previousTPPolicyIssuer"));
            entity.setPreviousTPPolicyNumber(formData.get("previousTPPolicyNumber"));

            // -------- Nominee Section (part of vehicle flow) --------
            entity.setNomineeName(formData.get("nomineeName"));
            entity.setNomineeDOB(formData.get("nomineeDOB"));
            entity.setRelationshipNominee(formData.get("relationshipNominee"));

            // -------- Appointee --------
            entity.setAppointeeName(formData.get("apointeeName"));
            entity.setAppointeeRelationship(formData.get("apointeeRelationship"));

            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            vehicleInformationFormRepository.save(entity);

            log.info(
                    "Vehicle Information form saved | txnId={} | submissionId={}",
                    transactionId,
                    submissionId
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error(
                    "Error saving Vehicle Information form | txnId={}",
                    transactionId,
                    e
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to submit Vehicle Information form");
        }
    }

    @Override
    public ResponseEntity<?> submitPersonalDetailsForm(
            Map<String, String> formData,
            String formUrl,
            String transactionId,
            String messageId) {

        try {
            // ================= SUBMIT FORM TO BPP =================
            ResponseEntity<String> responseBody =
                    submitForm(formData, formUrl);

            String response = responseBody.getBody();

            log.info(
                    "Personal details form submitted to BPP | txnId={} | msgId={} | response={}",
                    transactionId,
                    messageId,
                    response
            );

            if (response == null) {
                return ResponseEntity.badRequest()
                        .body("Empty response from personal details form service");
            }

            // ================= READ SUBMISSION ID =================
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse =
                    mapper.readTree(response);

            String submissionId =
                    jsonResponse.get("submission_id").asText();

            // ================= SAVE TO DB =================
            PersonalDetailsForm entity = new PersonalDetailsForm();

            entity.setTransactionId(transactionId);
            entity.setMessageId(messageId);
            entity.setFormId(formData.get("formId"));
            entity.setSubmissionId(submissionId);

            entity.setName(formData.get("name"));
            entity.setAddress(formData.get("address"));
            entity.setDob(formData.get("dob"));
            entity.setGender(formData.get("gender"));
            entity.setEmail(formData.get("email"));
            entity.setPhone(formData.get("phone"));

            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            personalDetailsFormRepository.save(entity);

            log.info(
                    "Personal details form saved successfully | txnId={} | submissionId={}",
                    transactionId,
                    submissionId
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error(
                    "Error saving personal details form | txnId={} | msgId={}",
                    transactionId,
                    messageId,
                    e
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving personal details form");
        }
    }


    @Override
    public ResponseEntity<?> submitProposerFormData(Map<String, String> formData, String formUrl, String transactionId, String messageId) {

        try {
            ResponseEntity<String> responseBody = submitForm(formData, formUrl);
            String response = responseBody.getBody();
            log.info("Form submitted successfully.Proceeding to save the details in proposer form table. response:{}", response);
            if (response != null) {
                ProposerForm proposer = new ProposerForm();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response);
                String submissionId = jsonResponse.get("submission_id").asText();

                proposer.setFirstName(formData.get("firstName"));
                proposer.setLastName(formData.get("lastName"));
                proposer.setAddress(formData.get("address"));
                proposer.setDob(formData.get("dob"));
                proposer.setGender(formData.get("gender"));
                proposer.setEmail(formData.get("email"));
                proposer.setPhone(formData.get("phone"));
                proposer.setPoliticallyExposedPerson(formData.get("politicallyExposedPerson"));
                proposer.setGstin(formData.get("gstin"));
                proposer.setHeight(formData.get("height"));
                proposer.setQuestion1(formData.get("question1"));
                proposer.setQuestion2(formData.get("question2"));
                proposer.setQuestion3(formData.get("question3"));
                proposer.setQuestion4_1(formData.get("question4_1"));
                proposer.setQuestion4_2(formData.get("question4_2"));
                proposer.setTransactionId(transactionId);
                proposer.setMessageId(messageId);
                proposer.setSubmissionId(submissionId);
                proposer.setCreatedAt(LocalDateTime.now());
                proposer.setUpdatedAt(LocalDateTime.now());
                proposerFormRepository.save(proposer);
            }
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving proposer form: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> submitNomineeFormData(Map<String, String> formData, String formUrl, String transactionId, String messageId) {

        try {
            ResponseEntity<String> responseBody = submitForm(formData, formUrl);
            String response = responseBody.getBody();
            log.info("Form submitted successfully.Proceeding to save the details in nominee form table. response:{}", response);
            if (response != null) {
                NomineeForm nominee = new NomineeForm();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response);
                String submissionId = jsonResponse.get("submission_id").asText();

                nominee.setFirstName(formData.get("firstName"));
                nominee.setLastName(formData.get("lastName"));
                nominee.setDob(formData.get("dob"));
                nominee.setRelation(formData.get("relation"));
                nominee.setTransactionId(transactionId);
                nominee.setMessageId(messageId);
                nominee.setSubmissionId(submissionId);
                nominee.setCreatedAt(LocalDateTime.now());
                nominee.setUpdatedAt(LocalDateTime.now());
                nomineeFormRepository.save(nominee);
            }
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving proposer form: " + e.getMessage());
        }

    }

    @Override
    public ResponseEntity<String> saveFamilyData(Map<String, Object> form, String formUrl, String transactionId, String messageId) {
        try {

            Map<String, String> formData = form.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> String.valueOf(e.getValue())
                    ));
            ResponseEntity<String> responseBody = submitForm(formData, formUrl);
            String response = responseBody.getBody();
            log.info("Form submitted successfully.Proceeding to save the details in family form table. response:{}", response);
            if (response != null) {

                FamilyForm data = new FamilyForm();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response);

                String submissionId = jsonResponse.get("submission_id").asText();

                data.setPED(formData.get("PED"));
                data.setAmount(formData.get("amount"));
                data.setBloodPressure(formData.get("bloodPressure"));
                data.setDiabetes(formData.get("diabetes"));
                data.setDob(formData.get("dob"));
                data.setEmail(formData.get("email"));
                data.setFirstName(formData.get("firstName"));
                data.setGender(formData.get("gender"));
                data.setHeartAilments(formData.get("heartAilments"));
                data.setHeight(formData.get("height"));
                data.setLastName(formData.get("lastName"));
                data.setOther(formData.get("other"));
                data.setPanIndia(formData.get("panIndia"));
                data.setPanValue(formData.get("panValue"));
                data.setPhone(formData.get("phone"));
                data.setPincode(formData.get("pincode"));
                data.setWeight(formData.get("weight"));
                data.setRelation(formData.get("relation"));
                data.setSubmissionId(submissionId);

                data.setTransactionId(transactionId);
                data.setMessageId(messageId);
                data.setCreatedAt(LocalDateTime.now());
                data.setUpdatedAt(LocalDateTime.now());
                familyFormRepository.save(data);

            }
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving individual form: " + e.getMessage());
        }
    }

    public static ResponseEntity<String> submitForm(Map<String, String> formData, String formUrl) {

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            StringBuilder formBody = new StringBuilder();
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                if (formBody.length() > 0) {
                    formBody.append("&");
                }
                formBody.append(entry.getKey()).append("=").append(entry.getValue());
            }
            HttpEntity<String> requestEntity = new HttpEntity<>(formBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(formUrl, HttpMethod.POST, requestEntity, String.class);
            return response;

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error submitting form: " + e.getMessage());
        }

    }
}
