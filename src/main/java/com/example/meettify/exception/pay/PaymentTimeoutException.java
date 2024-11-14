package com.example.meettify.exception.pay;

public class PaymentTimeoutException extends RuntimeException{
    public PaymentTimeoutException(String message) {
        super(message);
    }
}
