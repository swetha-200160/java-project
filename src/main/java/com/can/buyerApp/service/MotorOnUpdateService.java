package com.can.buyerApp.service;

import com.can.buyerApp.request.OnUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;


public interface MotorOnUpdateService {


    ResponseEntity<?> saveOnUpdate(OnUpdateRequest onUpdateRequest) throws JsonProcessingException;

}
