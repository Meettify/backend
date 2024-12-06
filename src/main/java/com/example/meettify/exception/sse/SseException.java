package com.example.meettify.exception.sse;
/*
 *   worker : 유요한
 *   work   : 결제 예외처리
 *   date   : 2024/12/06
 * */
public class SseException extends RuntimeException{
    public SseException(String message) {
        super(message);
    }
}
