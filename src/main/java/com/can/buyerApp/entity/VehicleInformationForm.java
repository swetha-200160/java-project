package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "vehicle_information_form")
public class VehicleInformationForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= CONTEXT =================
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "form_id", nullable = false)
    private String formId;

    @Column(name = "submission_id", nullable = false)
    private String submissionId;

    // ================= VEHICLE DETAILS =================
    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "chassis_number")
    private String chassisNumber;

    @Column(name = "engine_number")
    private String engineNumber;

    // ================= PREVIOUS POLICY =================
    @Column(name = "previous_policy_number")
    private String previousPolicyNumber;

    @Column(name = "previous_policy_issuer")
    private String previousPolicyIssuer;

    @Column(name = "previous_tp_policy_issuer")
    private String previousTPPolicyIssuer;

    @Column(name = "previous_tp_policy_number")
    private String previousTPPolicyNumber;

    // ================= NOMINEE (PART OF VEHICLE FLOW) =================
    @Column(name = "nominee_name")
    private String nomineeName;

    @Column(name = "nominee_dob")
    private String nomineeDOB;

    @Column(name = "relationship_nominee")
    private String relationshipNominee;

    // ================= APPOINTEE =================
    @Column(name = "appointee_name")
    private String appointeeName;

    @Column(name = "appointee_relationship")
    private String appointeeRelationship;

    // ================= AUDIT =================
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
