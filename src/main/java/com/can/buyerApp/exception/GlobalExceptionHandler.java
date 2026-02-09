package com.can.buyerApp.exception;

import com.can.buyerApp.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionIdNotFoundException.class)
    public ResponseEntity<?> handleTransactionIdNotFound(TransactionIdNotFoundException ex) {
        ApiResponse errorResponse = ApiResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(errorResponse);
    }



}
