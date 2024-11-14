package com.example.meettify.exception.pay;

public class PaymentConfirmException extends RuntimeException{
    public PaymentConfirmException(String message) {
        super(message);
    }

    public PaymentConfirmException(PaymentConfirmErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
