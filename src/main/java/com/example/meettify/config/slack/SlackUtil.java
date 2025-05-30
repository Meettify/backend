package com.example.meettify.config.slack;

import com.slack.api.Slack;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.BlockCompositions;
import com.slack.api.webhook.WebhookPayloads;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.slack.api.model.block.composition.BlockCompositions.plainText;

/*
 *   worker : 유요한
 *   work   : 에러 내용을 슬랙에 메시지를 보내기 위한 클래스
 *   date   : 2024/10/25
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class SlackUtil {
    @Value("${slack.webhook}")
    private String webhookUrl;
    private static final String NEW_LINE = "\n";
    private static final String DOUBLE_NEW_LINE = "\n\n";
    // Slack 클라이언트 인스턴스
    private final Slack slackClient = Slack.getInstance();

    // Slack으로 알림 보내기 (비동기)
    @Async
    public void sendAlert(Exception error, RequestInfo request) {
        // 메시지 내용인 LayoutBlock List 생성
        List<LayoutBlock> layoutBlocks = generateLayoutBlock(error, request);

        try {
            // 슬랙의 send API과 webhookURL을 통해 생성한 메시지 내용 전송
            slackClient.send(webhookUrl, WebhookPayloads
                    .payload(p -> p
                            // 메시지 전송 유저명
                            .text("Exception is detected 🚨")
                            // 메시지 내용
                            .blocks(layoutBlocks)));
        } catch (IOException e) {
            // 실패시 로그 기록
            log.warn("Slack alert failed: {}", e.getMessage(), e);
        }
    }

    // Slack 클라이언트 종료 처리
    @PreDestroy
    public void shutdownSlackClient() {
        try {
            slackClient.close(); // 클라이언트 리소스 정리
            log.info("Slack client successfully shut down.");
        } catch (Exception e) {
            log.error("Failed to shut down Slack client: {}", e.getMessage(), e);
        }
    }


    // 전체 메시지가 담긴 LayoutBlock 생성
    private List<LayoutBlock> generateLayoutBlock(Exception error, RequestInfo request) {
        return Blocks.asBlocks(
                getHeader(),
                Blocks.divider(),
                getSection(generateErrorMessage(error)),
                Blocks.divider(),
                getSection(generateErrorPointMessage(request)),
                Blocks.divider(),
                // 이슈 생성을 위해 프로젝트의 Issue URL을 입력하여 바로가기 링크를 생성
                getSection("<https://github.com/Meettify/backend/issues>|이슈 생성하러 가기")
        );
    }

    // 예외 정보 메시지 생성
    private String generateErrorMessage(Exception error) {
        // StringBuilder를 인스턴스 변수로 사용하지 않고, 메서드 내부에서 지역 변수로 선언
        // 이렇게 함으로써, 멀티스레드 환경에서 여러 스레드가 동시에 sendAlert 메서드를 호출할 때
        // 인스턴스 변수에 대한 접근으로 인해 발생할 수 있는 경쟁 상태를 피할 수 있습니다.
        StringBuilder sb = new StringBuilder(); // 지역 변수로 변경
        sb.append("*[🔥 Exception]*" + NEW_LINE).append(error.getMessage()).append(DOUBLE_NEW_LINE); // Exception의 메시지만 포함
        sb.append("*[📩 From]*" + NEW_LINE).append(readRootStackTrace(error)).append(DOUBLE_NEW_LINE);
        return sb.toString();
    }

    // HttpServletRequest를 사용하여 예외발생 요청에 대한 정보 메시지 생성
    private String generateErrorPointMessage(RequestInfo request) {
        StringBuilder sb = new StringBuilder(); // 지역 변수로 변경
        sb.append("*[🧾세부정보]*" + NEW_LINE);
        sb.append("Request URL : ").append(request.requestURL()).append(NEW_LINE);
        sb.append("Request Method : ").append(request.method()).append(NEW_LINE);
        sb.append("Request Time : ").append(LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))).append(NEW_LINE);
        return sb.toString();
    }

    // 예외발생 클래스 정보 return
    private String readRootStackTrace(Exception error) {
        return error.getStackTrace()[0].toString();
    }

    // 에러 로그 메시지의 제목 return
    private LayoutBlock getHeader() {
        return Blocks.header(h -> h.text(plainText(pt -> pt.emoji(true).text("서버 측 오류로 예상되는 예외 상황이 발생하였습니다."))));
    }

    // 에러 로그 메시지 내용 return
    private LayoutBlock getSection(String message) {
        return Blocks.section(s -> s.text(BlockCompositions.markdownText(message)));
    }
}