package com.example.meettify.exception.importResponse;

/*
 *   worker : 유요한
 *   work   : 결제 예외처리
 *   date   : 2024/11/08
 * */
public class IamportResponseException extends Exception {
    private static final long serialVersionUID = 1108159593517976752L;
    private final int httpStatusCode;

    // HTTP 상태 코드와 메시지를 직접 받는 생성자
    public IamportResponseException(String error, int httpStatusCode) {
        super(error);
        this.httpStatusCode = httpStatusCode;
    }

    // 기존 IamportResponseException을 래핑하는 생성자
    public IamportResponseException(IamportResponseException exception) {
        super(exception.getMessage(), exception);
        this.httpStatusCode = exception.getHttpStatusCode();
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
