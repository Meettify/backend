package com.example.meettify.repository.question;

import com.example.meettify.dto.question.ReplyStatus;
import com.example.meettify.dto.question.ResponseCountDTO;
import com.example.meettify.entity.member.QMemberEntity;
import com.example.meettify.entity.question.QuestionEntity;
import com.example.meettify.exception.board.BoardException;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public Page<QuestionEntity> findAllQuestions(Pageable page, ReplyStatus replyStatus) {
        try {
            JPAQuery<QuestionEntity> questions = queryFactory
                    .select(questionEntity)
                    .from(questionEntity)
                    .where(stateEq(replyStatus))
                    .join(questionEntity.member, memberEntity).fetchJoin()
                    .offset(page.getOffset())
                    .limit(page.getPageSize());

            JPAQuery<Long> count = queryFactory
                    .select(questionEntity.count())
                    .from(questionEntity)
                    .where(stateEq(replyStatus));
            // 동적 정렬
            sort(page, questions);
            List<QuestionEntity> result = questions.fetch();
            return PageableExecutionUtils.getPage(result, page, count::fetchOne);
        } catch (Exception e) {
            throw new BoardException("데이터베이스에서 문의글을 가져오는 것을 실패했습니다.");
        }
    }

    private static void sort(Pageable page, JPAQuery<QuestionEntity> questions) {
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
    }

    public BooleanExpression stateEq(ReplyStatus replyStatus) {
        return replyStatus == null ? null : questionEntity.replyStatus.eq(replyStatus);
    }

    @Override
    public Page<QuestionEntity> findAllByMember(String memberEmail, Pageable page, ReplyStatus replyStatus) {
        try {
            JPAQuery<QuestionEntity> questions = queryFactory
                    .selectFrom(questionEntity)
                    .where(stateEq(replyStatus), questionEntity.member.memberEmail.eq(memberEmail))
                    .join(questionEntity.member, memberEntity).fetchJoin()
                    .offset(page.getOffset())
                    .limit(page.getPageSize());

            JPAQuery<Long> count = queryFactory
                    .select(questionEntity.count())
                    .from(questionEntity)
                    .where(stateEq(replyStatus), questionEntity.member.memberEmail.eq(memberEmail));

            // 동적 정렬
            sort(page, questions);

            List<QuestionEntity> result = questions.fetch();
            return PageableExecutionUtils.getPage(result, page, count::fetchOne);
        } catch (Exception e) {
            throw new BoardException("데이터베이스에서 문의글을 가져오는 것을 실패했습니다.");
        }
    }

    @Override
    public ResponseCountDTO countMyQuestions(String email) {
        try {
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

    // 모든 문의글
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
