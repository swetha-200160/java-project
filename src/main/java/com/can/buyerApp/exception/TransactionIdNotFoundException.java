package com.can.buyerApp.exception;


public class TransactionIdNotFoundException extends RuntimeException {

    public TransactionIdNotFoundException(String message) {
        super(message);
    }
}
