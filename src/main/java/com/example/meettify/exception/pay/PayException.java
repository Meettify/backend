package com.example.meettify.exception.pay;
/*
 *   worker : 유요한
 *   work   : 결제 예외처리
 *   date   : 2024/11/12
 * */
public class PayException extends RuntimeException{
    public PayException(String message) {
        super(message);
    }
}
