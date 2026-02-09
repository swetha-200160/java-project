package com.can.buyerApp.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Entity
@Table(name = "motor_onselect")
public class MotorOnSelectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "transaction_id", nullable = false)
    private String transaction_id;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "parent_item_id")
    private String parentItemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_short_desc", columnDefinition = "TEXT")
    private String itemShortDesc;

    @Column(name = "item_images", columnDefinition = "TEXT")
    private String itemImages;

    @Column(name = "item_price")
    private String itemPrice;

    @Column(name = "item_currency")
    private String itemCurrency;

    // Vehicle and Policy Details (from tags)
    @Column(name = "vehicle_details", columnDefinition = "TEXT")
    private String vehicleDetails;

    @Column(name = "time_duration")
    private String timeDuration;

    @Column(name = "time_label")
    private String timeLabel;

    // XInput form details
    @Column(name = "xinput_required")
    private Boolean xinputRequired;

    @Column(name = "xinput_head_name")
    private String xinputHeadName;

    @Column(name = "xinput_headings", columnDefinition = "TEXT")
    private String xinputHeadings;

    @Column(name = "form_id")
    private String formId;

    @Column(name = "form_url")
    private String formUrl;

    @Column(name = "form_mime_type")
    private String formMimeType;

    @Column(name = "form_submission_id")
    private String formSubmissionId;

    @Column(name = "form_resubmit")
    private Boolean formResubmit;

    @Column(name = "form_multiple_submissions")
    private Boolean formMultipleSubmissions;

    @Column(name = "add_ons", columnDefinition = "TEXT")
    private String addOns;

    @Column(name = "quote_id")
    private String quoteId;

    @Column(name = "breakup_details", columnDefinition = "TEXT")
    private String breakupDetails;

    @Column(name = "total_price")
    private String totalPrice;

    @Column(name = "currency")
    private String currency;

    @Column(name = "ttl")
    private String ttl;

    // Provider details
    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "provider_short_desc", columnDefinition = "TEXT")
    private String providerShortDesc;

    @Column(name = "provider_long_desc", columnDefinition = "TEXT")
    private String providerLongDesc;

    @Column(name = "provider_images", columnDefinition = "TEXT")
    private String providerImages;

    // Fulfillment details
    @Column(name = "fulfillment_id")
    private String fulfillmentId;

    @Column(name = "fulfillment_ids", columnDefinition = "TEXT")
    private String fulfillmentIds;

    @Column(name = "fulfillment_type")
    private String fulfillmentType;

    @Column(name = "fulfillment_state")
    private String fulfillmentState;

    @Column(name = "customer_details", columnDefinition = "TEXT")
    private String customerDetails;

    @Column(columnDefinition = "TEXT")
    private String paymentUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper methods to deserialize the JSON strings into proper Java structures
    public Map<String, Object> getVehicleDetailsMap() {
        try {
            if (vehicleDetails != null) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(vehicleDetails, Map.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getAddOnsMap() {
        try {
            if (addOns != null) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(addOns, List.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> getBreakupDetailsMap() {
        try {
            if (breakupDetails != null) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(breakupDetails, Map.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> getCustomerDetailsMap() {
        try {
            if (customerDetails != null) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(customerDetails, Map.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getXinputHeadingsList() {
        try {
            if (xinputHeadings != null) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(xinputHeadings, List.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}