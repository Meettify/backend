package com.example.meettify.exception;

import com.example.meettify.config.slack.SlackNotification;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.cart.CartException;
import com.example.meettify.exception.comment.CommentException;
import com.example.meettify.exception.externalService.ExternalServiceException;
import com.example.meettify.exception.file.FileDownloadException;
import com.example.meettify.exception.file.FileUploadException;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.exception.order.OrderException;
import com.example.meettify.exception.sessionExpire.SessionExpiredException;
import com.example.meettify.exception.stock.OutOfStockException;
import com.example.meettify.exception.validation.DataValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 *   worker : 유요한
 *   work   : 전역으로 발생한 예외를 처리해줄 수 있는 Class를 생성
 *   date   : 2024/09/19
 *   update : 2024/10/18
 * */
@RestControllerAdvice
public class GlobalExceptionAdvice {
    // 표준 응답 형식을 위한 내부 클래스
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ErrorResponse {
        private String error;
        private String message;
    }

    // 전체적인 예외처리
    @ExceptionHandler(Exception.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Server Error", e.getMessage()));
    }

    // 유저 관련 발생하는 예외처리
    @ExceptionHandler(MemberException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Member Error", e.getMessage()));
    }

    // 게시글 관련 발생하는 예외처리
    @ExceptionHandler(BoardException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBoardException(BoardException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Board Error", e.getMessage()));
    }

    // 댓글 관련 예외처리
    @ExceptionHandler(CommentException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleCommentException(CommentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Comment Error", e.getMessage()));
    }

    // 상품 관련 예외처리
    @ExceptionHandler(ItemException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleItemException(ItemException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Item Error", e.getMessage()));
    }

    // 장바구니 관련 예외처리
    @ExceptionHandler(CartException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleCartException(CartException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Cart Error", e.getMessage()));
    }

    // 주문 관련 예외처리
    @ExceptionHandler(OrderException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleOrderException(OrderException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Order Error", e.getMessage()));
    }

    // 검증 오류 예외처리
    @ExceptionHandler(DataValidationException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleDataValidationException(DataValidationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Validation Error", e.getMessage()));
    }

    // 세션 만료 관련 예외처리
    @ExceptionHandler(SessionExpiredException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleSessionExpiredException(SessionExpiredException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Session Expired", e.getMessage()));
    }

    // 파일 업로드 예외처리
    @ExceptionHandler(FileUploadException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("File Upload Error", e.getMessage()));
    }

    // 파일 다운로드 예외처리
    @ExceptionHandler(FileDownloadException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleFileDownloadException(FileDownloadException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("File Download Error", e.getMessage()));
    }

    // 외부 서비스 예외처리
    @ExceptionHandler(ExternalServiceException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(ExternalServiceException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("External Service Error", e.getMessage()));
    }

    // 재고 부족 관련 예외처리
    @ExceptionHandler(OutOfStockException.class)
    @SlackNotification
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleOutOfStockException(OutOfStockException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Out of Stock", e.getMessage()));
    }
}
