package com.example.meettify.exception.importResponse;

import org.apache.http.client.HttpResponseException;

/*
 *   worker : 유요한
 *   work   : 결제 예외처리
 *   date   : 2024/11/08
 * */
public class IamportResponseException extends Exception{
    private static final long serialVersionUID = 1108159593517976752L;
    private int httpStatusCode;

    public IamportResponseException(String error, HttpResponseException e) {
        super(error, e);
        this.httpStatusCode = e.getStatusCode(); // getStatusCode() 메서드 사용
    }

    // IamportResponseException을 인자로 받는 새로운 생성자
    public IamportResponseException(IamportResponseException exception) {
        super(exception.getMessage(), exception);
        this.httpStatusCode = exception.getHttpStatusCode();
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
