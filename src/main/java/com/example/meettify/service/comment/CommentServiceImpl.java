package com.example.meettify.service.comment;

import com.example.meettify.config.metric.TimeTrace;
import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.ResponseCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;
import com.example.meettify.entity.comment.CommentEntity;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.comment.CommentException;
import com.example.meettify.repository.comment.CommentRepository;
import com.example.meettify.repository.community.CommunityRepository;
import com.example.meettify.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@RequiredArgsConstructor
@Service
@Log4j2
public class CommentServiceImpl implements  CommentService{
    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;

    // 댓글 생성
    @Override
    @TimeTrace
    public ResponseCommentDTO createComment(Long communityId,
                                            CreateCommentDTO comment,
                                            String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 커뮤니티 조회
            CommunityEntity findCommunity = communityRepository.findById(communityId)
                    .orElseThrow(() -> new BoardException("커뮤니티가 존재하지 않습니다."));

            CommentEntity parentComment;
            CommentEntity commentEntity;
            Long parentCommentId = 0L;

            if (comment.getCommentParentId() != null) {
                // 부모 댓글이 있는 경우 대댓글로 설정
                parentComment = commentRepository.findById(comment.getCommentParentId())
                        .orElseThrow(() -> new CommentException("부모 댓글이 존재하지 않습니다."));
                // 댓글 생성하기 위한 엔티티
                commentEntity = CommentEntity.saveComment(comment, findMember, findCommunity, parentComment);
                // 부모 댓글에 자식 댓글을 넣음
                parentComment.getChildren().add(commentEntity);
            }

            // 댓글 생성하기 위한 엔티티
            // 부모 댓글을 넘겨받지 않는다는 것은 일반 댓글이니 여기서 처리
            commentEntity = CommentEntity.saveComment(comment, findMember, findCommunity, null);
            // 댓글 디비에 저장
            CommentEntity saveComment = commentRepository.save(commentEntity);

            if(saveComment.getParent() != null) {
                parentCommentId = saveComment.getParent().getCommentId();
            }

            ResponseCommentDTO response = ResponseCommentDTO.changeDTO(saveComment, findMember.getNickName(), parentCommentId);
            log.info("반환 댓글 : " + response);
            return response;
        } catch (Exception e) {
            log.error("댓글 생성하는데 실패했습니다. {} ", e.getMessage());
            throw new CommentException("댓글 생성하는데 실패했습니다. : "+ e.getMessage());
        }
    }

    // 뎃글 수정
    @Override
    @TimeTrace
    public ResponseCommentDTO updateComment(Long commentId, Long communityId, UpdateCommentDTO comment, String email) {
        try {
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            CommunityEntity findCommunity = communityRepository.findById(communityId)
                    .orElseThrow(() -> new BoardException("커뮤니티 글이 없습니다."));

            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentException("댓글이 없습니다."));


            // 댓글 수정
            findComment.updateComment(comment);
            CommentEntity updateComment = commentRepository.save(findComment);
            ResponseCommentDTO response = ResponseCommentDTO.changeDTO(updateComment, findMember.getNickName(), findComment.getParent().getCommentId());
            log.info("수정된 댓글 {} ", response );
            return response;
        } catch (Exception e) {
            throw new CommentException("댓글 수정하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 댓글 삭제
    @Override
    @TimeTrace
    public String deleteComment(Long commentId) {
        try {
            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentException("댓글을 찾지 못했습니다."));

            if(findComment != null) {
                commentRepository.deleteById(commentId);
                return "댓글 삭제 성공";
            }
            throw new CommentException("댓글이 존재하지 않습니다. 잘못된 id를 보냈습니다.");
        } catch (Exception e) {
            throw new CommentException("댓글 삭제하는데 실패했습니다.");
        }
    }

    // 댓글 상세페이지
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public ResponseCommentDTO getComment(Long commentId) {
        try {
            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentException("댓글이 존재하지 않습니다."));
            return ResponseCommentDTO.changeDTO(findComment, findComment.getMember().getNickName(), findComment.getParent().getCommentId());
        } catch (Exception e) {
            throw new CommentException("댓글 조회하는데 실패했습니다.");
        }
    }

    // 댓글 페이징처리
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public Page<ResponseCommentDTO> getComments(Pageable page, Long communityId) {
        try {
            // 댓글과 대댓글을 모두 가져옴
            Page<CommentEntity> findAllComment = commentRepository.findCommentByCommunityId(communityId, page);
            return findAllComment.map(commentEntity -> ResponseCommentDTO.changeDTO(
                    commentEntity, commentEntity.getMember().getNickName(), commentEntity.getParent().getCommentId()));
        } catch (Exception e) {
            throw new CommentException("상품을 페이징 처리해서 가져오는데 실패했습니다.");
        }
    }
}
