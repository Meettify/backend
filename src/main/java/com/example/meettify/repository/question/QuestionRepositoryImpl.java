package com.example.meettify.repository.question;

import com.example.meettify.dto.question.ReplyStatus;
import com.example.meettify.dto.question.ResponseCountDTO;
import com.example.meettify.entity.member.QMemberEntity;
import com.example.meettify.entity.question.QuestionEntity;
import com.example.meettify.exception.board.BoardException;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.meettify.entity.member.QMemberEntity.memberEntity;
import static com.example.meettify.entity.question.QQuestionEntity.questionEntity;

@RequiredArgsConstructor
@Log4j2
public class QuestionRepositoryImpl implements CustomQuestionRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<QuestionEntity> findAllQuestions(Pageable page) {
        try {
            JPAQuery<QuestionEntity> questions = queryFactory
                    .select(questionEntity)
                    .from(questionEntity)
                    .join(questionEntity.member, memberEntity).fetchJoin()
                    .offset(page.getOffset())
                    .limit(page.getPageSize());

            JPAQuery<Long> count = queryFactory
                    .select(questionEntity.count())
                    .from(questionEntity);

            for (Sort.Order order : page.getSort()) {
                PathBuilder pathBuilder = new PathBuilder(
                        questionEntity.getType(),
                        questionEntity.getMetadata()
                );
                PathBuilder sort = pathBuilder.get(order.getProperty());

                questions.orderBy(
                        new OrderSpecifier<>(
                                order.isDescending() ? Order.DESC : Order.ASC,
                                sort != null ? sort : questionEntity.questionId
                        )
                );
            }
            List<QuestionEntity> result = questions.fetch();
            return PageableExecutionUtils.getPage(result, page, count::fetchOne);
        } catch (Exception e) {
            throw new BoardException("데이터베이스에서 문의글을 가져오는 것을 실패했습니다.");
        }
    }


    @Override
    public ResponseCountDTO countMyQuestions(String email) {
        try {
//            // 전체 문의글 수
//            long totalQuestions = Optional.ofNullable(
//                    queryFactory
//                            .select(questionEntity.count())
//                            .from(questionEntity)
//                            .where(questionEntity.member.memberEmail.eq(email))
//                            .fetchOne()
//            ).orElse(0L);
//            // 답글 완료 수 (REPLY_O)
//            long completedReplies = Optional.ofNullable(
//                    queryFactory
//                            .select(questionEntity.count())
//                            .from(questionEntity)
//                            .where(questionEntity.replyStatus.eq(ReplyStatus.REPLY_O),
//                                    questionEntity.member.memberEmail.eq(email))
//                            .fetchOne()
//            ).orElse(0L);
//            // 답글 미완료 수 (REPLY_X)
//
//            long pendingReplies = Optional.ofNullable(
//                    queryFactory
//                            .select(questionEntity.count())
//                            .from(questionEntity)
//                            .where(questionEntity.replyStatus.eq(ReplyStatus.REPLY_X),
//                                    questionEntity.member.memberEmail.eq(email))
//                            .fetchOne()
//            ).orElse(0L);
            Map<ReplyStatus, Long> count = queryFactory
                    .select(questionEntity.replyStatus, questionEntity.count())
                    .from(questionEntity)
                    .where(questionEntity.member.memberEmail.eq(email)) // 조건 추가
                    .groupBy(questionEntity.replyStatus)
                    .fetch()
                    .stream()
                    .collect(Collectors.toMap(
                            tuple -> tuple.get(0, ReplyStatus.class),
                            tuple -> tuple.get(1, Long.class)
                    ));

            Result result = getResult(count);

            return ResponseCountDTO.of(result.totalQuestions(), result.completedReplies(), result.pendingReplies());
        } catch (Exception e) {
            throw new BoardException("데이터베이스에서 문의글 수량을 가져오는 것을 실패했습니다.");
        }
    }

    private static @NotNull Result getResult(Map<ReplyStatus, Long> count) {
        long totalQuestions = count.values().stream().mapToLong(Long::longValue).sum();
        long completedReplies = count.getOrDefault(ReplyStatus.REPLY_O, 0L);
        long pendingReplies = count.getOrDefault(ReplyStatus.REPLY_X, 0L);
        Result result = new Result(totalQuestions, completedReplies, pendingReplies);
        return result;
    }

    private record Result(long totalQuestions, long completedReplies, long pendingReplies) {
    }

    // 모든 문의글 카운트
    @Override
    public ResponseCountDTO countAllQuestions() {
        try {
            Map<ReplyStatus, Long> count = queryFactory
                    .select(questionEntity.replyStatus, questionEntity.count())
                    .from(questionEntity)
                    .groupBy(questionEntity.replyStatus)
                    .fetch()
                    .stream()
                    .collect(Collectors.toMap(
                            tuple -> tuple.get(0, ReplyStatus.class),
                            tuple -> tuple.get(1, Long.class)
                    ));

            Result result = getResult(count);
            return ResponseCountDTO.of(result.totalQuestions(), result.completedReplies(), result.pendingReplies());
        } catch (Exception e) {
            throw new BoardException("데이터베이스에서 문의글 수량을 가져오는 것을 실패했습니다.");
        }
    }
}
