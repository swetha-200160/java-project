//package com.can.buyerApp.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "motor_manual_review_form")
//@Getter
//@Setter
//public class MotorManualReviewForm {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String reviewReason;
//    private String inspectionRequired;
//    private String inspectionDate;
//    private String inspectionLocation;
//    private String odometerReading;
//    private String rcAvailable;
//    private String hypothecation;
//    private String previousClaimDetails;
//    private String remarks;
//    private String additionalNotes;
//
//    private String submissionId;
//    private String transactionId;
//    private String messageId;
//
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//}

package com.can.buyerApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "motor_manual_review_form")
@Getter
@Setter
public class MotorManualReviewForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String remarks;

    private String transactionId;
    private String messageId;
    private String submissionId;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
 
