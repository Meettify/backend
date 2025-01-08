package com.example.meettify.service.comment;

import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.ResponseCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;
import com.example.meettify.entity.comment.CommentEntity;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.comment.CommentException;
import com.example.meettify.repository.jpa.comment.CommentRepository;
import com.example.meettify.repository.jpa.community.CommunityRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
@Log4j2
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;

    // 댓글 생성
    @Override
    public ResponseCommentDTO createComment(Long communityId, CreateCommentDTO comment, String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 커뮤니티 조회
            CommunityEntity findCommunity = communityRepository.findById(communityId)
                    .orElseThrow(() -> new BoardException("커뮤니티가 존재하지 않습니다."));

            // 댓글 생성 로직
            CommentEntity commentEntity;
            if (comment.getCommentParentId() != null) {
                // 부모 댓글이 있는 경우 대댓글로 설정
                CommentEntity parentComment = commentRepository.findById(comment.getCommentParentId())
                        .orElseThrow(() -> new CommentException("부모 댓글이 존재하지 않습니다."));

                // 자식 댓글 설정
                commentEntity = CommentEntity.saveComment(comment, findMember, findCommunity, parentComment);
                parentComment.getChildren().add(commentEntity);
            } else {
                // 일반 댓글인 경우
                commentEntity = CommentEntity.saveComment(comment, findMember, findCommunity, null);
            }

            // 댓글 디비에 저장
            CommentEntity savedComment = commentRepository.save(commentEntity);
            Long parentCommentId = (savedComment.getParent() != null) ? savedComment.getParent().getCommentId() : 0L;

            ResponseCommentDTO response = ResponseCommentDTO.changeDTO(savedComment, findMember.getNickName(), parentCommentId);
            log.info("반환 댓글 : " + response);
            return response;
        } catch (Exception e) {
            log.error("댓글 생성하는데 실패했습니다. {}", e.getMessage());
            throw new CommentException("댓글 생성하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 댓글 수정
    @Override
    public ResponseCommentDTO updateComment(Long commentId, Long communityId, UpdateCommentDTO comment, String email) {
        try {
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            CommunityEntity findCommunity = communityRepository.findById(communityId)
                    .orElseThrow(() -> new BoardException("커뮤니티 글이 없습니다."));
            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentException("댓글이 없습니다."));

            // 댓글 수정
            findComment.updateComment(comment);
            CommentEntity updatedComment = commentRepository.save(findComment);
            Long parentCommentId = (findComment.getParent() != null) ? findComment.getParent().getCommentId() : 0L;
            ResponseCommentDTO response = ResponseCommentDTO.changeDTO(updatedComment, findMember.getNickName(), parentCommentId);
            log.info("수정된 댓글 {} ", response);
            return response;
        } catch (Exception e) {
            log.error("댓글 수정 실패: {}", e.getMessage());
            throw new CommentException("댓글 수정하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 댓글 삭제
    @Override
    public String deleteComment(Long commentId) {
        try {
            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentException("댓글을 찾지 못했습니다."));

            commentRepository.deleteById(commentId);
            return "댓글 삭제 성공";
        } catch (Exception e) {
            log.error("댓글 삭제 실패: {}", e.getMessage());
            throw new CommentException("댓글 삭제하는데 실패했습니다.");
        }
    }

    // 댓글 상세페이지
    @Override
    @Transactional(readOnly = true)
    public ResponseCommentDTO getComment(Long commentId) {
        try {
            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentException("댓글이 존재하지 않습니다."));
            Long parentCommentId = (findComment.getParent() != null) ? findComment.getParent().getCommentId() : 0L;
            return ResponseCommentDTO.changeDTO(findComment, findComment.getMember().getNickName(), parentCommentId);
        } catch (Exception e) {
            log.error("댓글 조회 실패: {}", e.getMessage());
            throw new CommentException("댓글 조회하는데 실패했습니다.");
        }
    }

    // 댓글 페이징 처리
    @Override
    @Transactional(readOnly = true)
    public Page<ResponseCommentDTO> getComments(Pageable page, Long communityId) {
        try {
            Page<CommentEntity> findAllComment = commentRepository.findCommentByCommunityId(communityId, page);
            return findAllComment.map(commentEntity -> {
                Long parentCommentId = (commentEntity.getParent() != null) ? commentEntity.getParent().getCommentId() : 0L;
                return ResponseCommentDTO.changeDTO(commentEntity, commentEntity.getMember().getNickName(), parentCommentId);
            });
        } catch (Exception e) {
            log.error("댓글 페이징 조회 실패: {}", e.getMessage());
            throw new CommentException("댓글을 페이징 처리해서 가져오는데 실패했습니다.");
        }
    }
}
