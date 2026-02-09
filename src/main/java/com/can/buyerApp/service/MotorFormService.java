//package com.can.buyerApp.service;
//
//import java.util.Map;
//
//import org.springframework.http.ResponseEntity;
//
//public interface MotorFormService {
//	/**
//     * Submit Motor Vehicle Details Form (FIRST FORM - after ON_INIT_1)
//     * Contains: registration number, chassis, engine, previous policy details
//     */
//    ResponseEntity<?> submitMotorVehicleForm(Map<String, String> formData, 
//                                              String formUrl, 
//                                              String transactionId, 
//                                              String messageId);
//    /**
//     * Submit Motor Nominee Details Form (SECOND FORM - after ON_INIT_2)
//     * Contains: nominee name, DOB, relationship, appointee details
//     */
//    ResponseEntity<?> submitMotorNomineeForm(Map<String, String> formData, 
//                                              String formUrl, 
//                                              String transactionId, 
//                                              String messageId);
//    
//
//}
