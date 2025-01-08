package com.example.meettify.repository.jpa.comment;


import com.example.meettify.entity.comment.CommentEntity;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.meettify.entity.comment.QCommentEntity.commentEntity;
import static com.example.meettify.entity.community.QCommunityEntity.communityEntity;
import static com.example.meettify.entity.member.QMemberEntity.memberEntity;

@RequiredArgsConstructor
@Log4j2
public class CommentRepositoryImpl implements CustomCommentRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentEntity> findCommentByCommunityId(Long communityId, Pageable pageable) {
        List<CommentEntity> comment = queryFactory
                .selectFrom(commentEntity)
                .leftJoin(commentEntity.parent).fetchJoin()
                .leftJoin(commentEntity.community, communityEntity).fetchJoin()
                .leftJoin(commentEntity.member, memberEntity).fetchJoin() // member fetch join
                .where(commentEntity.community.communityId.eq(communityId))
                .orderBy(commentEntity.commentId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        log.info("comment count: {}", comment.size());
        log.info("comment: {}", comment);

        JPAQuery<Long> count = queryFactory
                .select(commentEntity.count())
                .from(commentEntity)
                .where(commentEntity.community.communityId.eq(communityId));

        log.info("comment count: {}", count::fetchOne);

        return PageableExecutionUtils.getPage(comment, pageable, count::fetchOne);
    }

}
