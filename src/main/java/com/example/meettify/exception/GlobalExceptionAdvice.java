package com.example.meettify.exception;

import com.example.meettify.config.slack.RequestInfo;
import com.example.meettify.config.slack.SlackUtil;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.cart.CartException;
import com.example.meettify.exception.chat.ChatException;
import com.example.meettify.exception.chat.ChatRoomException;
import com.example.meettify.exception.comment.CommentException;
import com.example.meettify.exception.externalService.ExternalServiceException;
import com.example.meettify.exception.file.FileDownloadException;
import com.example.meettify.exception.file.FileUploadException;
import com.example.meettify.exception.importResponse.IamportResponseException;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.exception.meet.MeetException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.exception.order.OrderException;
import com.example.meettify.exception.pay.PayException;
import com.example.meettify.exception.pay.PaymentConfirmErrorCode;
import com.example.meettify.exception.pay.PaymentConfirmException;
import com.example.meettify.exception.pay.PaymentTimeoutException;
import com.example.meettify.exception.sessionExpire.SessionExpiredException;
import com.example.meettify.exception.stock.OutOfStockException;
import com.example.meettify.exception.not_found.ResourceNotFoundException;
import com.example.meettify.exception.timeout.RequestTimeoutException;
import com.example.meettify.exception.validation.DataValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;

/*
 *   worker : 유요한
 *   work   : 전역으로 발생한 예외를 처리해줄 수 있는 Class를 생성
 *   date   : 2024/09/19
 *   update : 2024/10/25
 * */
@Log4j2
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice {
    private final SlackUtil slackUtil;
    private final LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

    // 표준 응답 형식을 위한 내부 클래스
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Log4j2
    @Setter
    private static class ErrorResponse {
        private String error;
        private String message;
        private LocalDateTime timestamp; // 오류 발생 시간
        private String path;     // 요청된 경로
        private String method;   // 요청 메서드
    }

    // 회원 관련 예외 처리
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> handleMemberException(MemberException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("회원 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * 게시글작성시 모든값이 제대로 입력되지 않았을때 발생하는 예외
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> dataIntegrityViolationError(DataIntegrityViolationException e, HttpServletRequest request){
        String message = "값이 제대로 입력되지 않았습니다.(DataIntegrityViolationException)";
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("회원 에러 발생");
        errorResponse.setMessage(message);
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 실제 결제금액과 DB의 결제 금액이 다를때 발생하는 예외
     * @return
     */
    @ExceptionHandler(IamportResponseException.class)
    public ResponseEntity<String> verifyIamportException(IamportResponseException e, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("회원 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송
        return new ResponseEntity<>("실제 결제금액과 서버에서 결제금액이 다릅니다.",HttpStatus.BAD_REQUEST);
    }



    // 게시판 관련 예외 처리
    @ExceptionHandler(BoardException.class)
    public ResponseEntity<ErrorResponse> handleBoardException(BoardException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("게시글 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 채팅방 예외처리
    @ExceptionHandler(ChatRoomException.class)
    public ResponseEntity<ErrorResponse> handleBoardException(ChatRoomException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("채팅방 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 채팅 예외처리
    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorResponse> handleBoardException(ChatException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("채팅방 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 모임 관련 예외 처리
    @ExceptionHandler(MeetException.class)
    public ResponseEntity<ErrorResponse> handleBoardException(MeetException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("모임 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 댓글 관련 예외 처리
    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ErrorResponse> handleCommentException(CommentException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("댓글 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 상품 관련 예외 처리
    @ExceptionHandler(ItemException.class)
    public ResponseEntity<ErrorResponse> handleItemException(ItemException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("상품 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 장바구니 관련 예외 처리
    @ExceptionHandler(CartException.class)
    public ResponseEntity<ErrorResponse> handleCartException(CartException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("장바구니 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 주문 관련 예외 처리
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorResponse> handleOrderException(OrderException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("주문 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 결제 관련 예외 처리
    @ExceptionHandler(PayException.class)
    public ResponseEntity<ErrorResponse> handleOrderException(PayException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("결제 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 결제 관련 예외 처리
    @ExceptionHandler(PaymentTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleOrderException(PaymentTimeoutException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("TOSS 결제 시간 초과 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 결제 관련 예외 처리
    @ExceptionHandler(PaymentConfirmException.class)
    public ResponseEntity<ErrorResponse> handleOrderException(PaymentConfirmException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("TOSS 결제 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 데이터 검증 예외 처리
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleDataValidationException(DataValidationException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("데이터 검증 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 세션 만료 예외 처리
    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ErrorResponse> handleSessionExpiredException(SessionExpiredException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("세션 만료 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    // 파일 업로드 예외 처리
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("파일 업로드 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    // 파일 다운로드 예외 처리
    @ExceptionHandler(FileDownloadException.class)
    public ResponseEntity<ErrorResponse> handleFileDownloadException(FileDownloadException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("파일 다운로드 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    // 외부 서비스 예외 처리
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(ExternalServiceException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("외부 서비스 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse);
    }

    // 재고 부족 예외 처리
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStockException(OutOfStockException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("재고부족 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 존재하지 않는 리소스에 대한 접근 시 발생
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("존재하지 않는 리소스에 접근해서 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }


    // 허용되지 않는 HTTP 메서드를 사용할 경우
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e,
                                                                         HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("허용되지 않는 HTTP 메서드 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(errorResponse);
    }

    // 클라이언트의 잘못된 요청 (예: 형식 불일치, 필수 값 누락 등)에서 발생
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e,
                                                                        HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("잘못된 요청 에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 요청이 특정 시간 내에 처리되지 못할 때 발생
    @ExceptionHandler(RequestTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleRequestTimeoutException(RequestTimeoutException e,
                                                                       HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("응답 시간 에러 발생(오래걸려서 TimeOut)");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.REQUEST_TIMEOUT)
                .body(errorResponse);
    }

    // 일반 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("에러 발생");
        errorResponse.setMessage(e.getMessage());
        errorResponse.setTimestamp(now); // 오류 발생 시간
        errorResponse.setPath(request.getRequestURI()); // 요청된 경로
        errorResponse.setMethod(request.getMethod()); // 요청 메서드

        slackUtil.sendAlert(e, new RequestInfo(request)); // Slack 알림 전송

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}