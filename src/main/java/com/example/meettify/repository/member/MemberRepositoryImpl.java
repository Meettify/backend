package com.example.meettify.repository.member;


import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.member.QMemberEntity;
import com.example.meettify.exception.member.MemberException;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.meettify.entity.member.QMemberEntity.memberEntity;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Log4j2
public class MemberRepositoryImpl implements CustomMemberRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberEntity> findAll(Pageable pageable, String memberEmail) {
        try {
            List<MemberEntity> members = queryFactory
                    .selectFrom(memberEntity)
                    .where(memberEmailEq(memberEmail))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(memberEntity.memberId.desc())
                    .fetch();

            JPAQuery<Long> count = queryFactory
                    .select(memberEntity.count())
                    .from(memberEntity)
                    .where(memberEmailEq(memberEmail));

            // 페이지 시작이거나 컨텐츠의 사이즈가 페이지 사이즈보다 작거나
            // 마지막 페이지일 때 카운트 쿼리를 호출하지 않는다.
            return PageableExecutionUtils.getPage(members, pageable, count::fetchOne);
        } catch (Exception e) {
            throw new MemberException("데이터베이스에서 회원들 데이터들을 가져오는데 실패했습니다.");
        }
    }

    private BooleanExpression memberEmailEq(String memberEmail) {
        return (!hasText(memberEmail)) ? null : memberEntity.memberEmail.likeIgnoreCase("%" + memberEmail + "%");
    }
}
