package com.example.meettify.config.websocket;

import com.example.meettify.exception.stomp.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

// STOMP 프로토콜의 에러를 처리하기 위한 핸들러
@Component
public class StompExceptionHandler extends StompSubProtocolErrorHandler {
    private static final byte[] EMPTY_PAYLOAD = new byte[0]; // 빈 메시지 페이로드를 정의

    public StompExceptionHandler() {
        super(); // 부모 클래스의 기본 생성자 호출
    }

    /**
     * 클라이언트 메시지 처리 중 발생한 에러를 처리합니다.
     *
     * @param clientMessage 클라이언트로부터 받은 메시지
     * @param ex            발생한 예외
     * @return 에러를 포함한 메시지
     */
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage,
                                                              Throwable ex) {

        // 발생한 예외를 변환
        final Throwable exception = converterTrowException(ex);

        // UnauthorizedException 발생 시 별도 처리
        if (exception instanceof UnauthorizedException) {
            return handleUnauthorizedException(clientMessage, exception);
        }

        // 기본 처리 방식으로 예외 전달
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    /**
     * 예외를 변환하는 메서드
     *
     * @param exception 발생한 예외
     * @return 변환된 예외
     */
    private Throwable converterTrowException(final Throwable exception) {
        if (exception instanceof MessageDeliveryException) {
            return exception.getCause(); // MessageDeliveryException 내부 원인 반환
        }
        return exception; // 기본 예외 반환
    }

    /**
     * UnauthorizedException을 처리하는 메서드
     *
     * @param clientMessage 클라이언트 메시지
     * @param ex            발생한 예외
     * @return 에러 메시지를 포함한 STOMP 메시지
     */
    private Message<byte[]> handleUnauthorizedException(Message<byte[]> clientMessage,
                                                        Throwable ex) {

        // Unauthorized 상태 코드를 포함한 메시지 생성
        return prepareErrorMessage(clientMessage, ex.getMessage(), HttpStatus.UNAUTHORIZED.name());
    }

    /**
     * 에러 메시지를 준비하는 메서드
     *
     * @param clientMessage 클라이언트 메시지
     * @param message       에러 메시지
     * @param errorCode     에러 코드
     * @return 에러 메시지를 포함한 STOMP 메시지
     */
    private Message<byte[]> prepareErrorMessage(final Message<byte[]> clientMessage,
                                                final String message, final String errorCode) {

        // STOMP 명령 ERROR 생성
        final StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(errorCode); // 에러 코드 설정
        accessor.setLeaveMutable(true); // 헤더를 변경 가능 상태로 설정

        // 클라이언트 메시지에서 수신 확인 ID 설정
        setReceiptIdForClient(clientMessage, accessor);

        // 에러 메시지와 헤더를 포함한 STOMP 메시지 반환
        return MessageBuilder.createMessage(
                message != null ? message.getBytes(StandardCharsets.UTF_8) : EMPTY_PAYLOAD,
                accessor.getMessageHeaders()
        );
    }

    /**
     * 클라이언트 메시지에서 Receipt ID를 추출하여 에러 메시지에 포함
     *
     * @param clientMessage 클라이언트 메시지
     * @param accessor      에러 메시지의 헤더 액세서
     */
    private void setReceiptIdForClient(final Message<byte[]> clientMessage,
                                       final StompHeaderAccessor accessor) {

        if (Objects.isNull(clientMessage)) {
            return; // 클라이언트 메시지가 없으면 반환
        }

        // 클라이언트 헤더에서 Receipt ID를 가져옴
        final StompHeaderAccessor clientHeaderAccessor = MessageHeaderAccessor.getAccessor(
                clientMessage, StompHeaderAccessor.class);

        final String receiptId =
                Objects.isNull(clientHeaderAccessor) ? null : clientHeaderAccessor.getReceipt();

        // Receipt ID가 있으면 에러 메시지에 설정
        if (receiptId != null) {
            accessor.setReceiptId(receiptId);
        }
    }

    /**
     * 내부적으로 발생한 에러를 처리
     *
     * @param errorHeaderAccessor 에러 메시지의 헤더
     * @param errorPayload        에러 메시지의 페이로드
     * @param cause               예외 원인
     * @param clientHeaderAccessor 클라이언트 헤더
     * @return 에러 메시지를 포함한 STOMP 메시지
     */
    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor,
                                             byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
        return MessageBuilder.createMessage(errorPayload, errorHeaderAccessor.getMessageHeaders());
    }
}
