package com.can.buyerApp.service;


import com.can.buyerApp.request.OnInitRequest;
import org.springframework.http.ResponseEntity;

public interface MotorOnInitService {
    ResponseEntity<?> saveOnInitRequest(OnInitRequest onInitRequest);
}
