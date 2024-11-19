package com.example.meettify.repository.question;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

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
}
