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
import com.example.meettify.repository.jpa.comment.CommentRepository;
import com.example.meettify.repository.jpa.community.CommunityRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
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
            CommentEntity parentComment;
            CommentEntity childComment;
            // 댓글 디비에 저장
            CommentEntity savedComment;
            if (comment.getCommentParentId() != null) {
                // 부모 댓글이 있는 경우 대댓글로 설정
                parentComment = commentRepository.findById(comment.getCommentParentId())
                        .orElseThrow(() -> new CommentException("부모 댓글이 존재하지 않습니다."));

                log.debug("부모 댓글 확인 {}", parentComment);

                // 자식 댓글 설정
                childComment = CommentEntity.saveComment(
                        comment,
                        findMember,
                        findCommunity,
                        parentComment);
                parentComment.addChildren(childComment);
                savedComment = commentRepository.save(childComment);
            } else {
                log.debug("일반 댓글 작성 동작");
                // 일반 댓글인 경우
                parentComment = CommentEntity.saveComment(
                        comment,
                        findMember,
                        findCommunity,
                        null);
                savedComment = commentRepository.save(parentComment);
            }

            log.info("디비 저장 후 엔티티 체크 {}", savedComment);
            Long parentCommentId = (Objects.requireNonNull(savedComment).getParent() != null) ?
                    savedComment.getParent().getCommentId() : 0L;

            ResponseCommentDTO response = ResponseCommentDTO.changeDTO(
                    savedComment,
                    findMember.getNickName(),
                    parentCommentId);
            log.debug("반환 댓글 {} ", response);
            return response;
        } catch (Exception e) {
            log.warn("댓글 생성하는데 실패했습니다. {}", e.getMessage());
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
            Long parentCommentId = (findComment.getParent() != null)
                    ? findComment.getParent().getCommentId() : 0L;
            ResponseCommentDTO response = ResponseCommentDTO.changeDTO(
                    findComment, findMember.getNickName(), parentCommentId);
            log.debug("수정된 댓글 {} ", response);
            return response;
        } catch (Exception e) {
            log.warn("댓글 수정 실패: {}", e.getMessage());
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
            log.warn("댓글 삭제 실패: {}", e.getMessage());
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
            Long parentCommentId = (findComment.getParent() != null) ? findComment.getParent().getCommentId() : 0L;
            return ResponseCommentDTO.changeDTO(findComment, findComment.getMember().getNickName(), parentCommentId);
        } catch (Exception e) {
            log.warn("댓글 조회 실패: {}", e.getMessage());
            throw new CommentException("댓글 조회하는데 실패했습니다.");
        }
    }

    // 댓글 페이징 처리
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public Page<ResponseCommentDTO> getComments(Pageable page, Long communityId) {
        try {
            // 부모 댓글 조회 (커뮤니티 상세 페이지에 속한 댓글들)
            // 부모 댓글과 바로 부모 댓글에 속한 자식 댓글만 가져옴
            Page<CommentEntity> findParentComment = commentRepository.findParentCommentsByCommunityId(communityId, page);

            // 자식 댓글에 대댓글이 달려있는 경우를 위해서 id를 추츨
            List<Long> childrenId = findParentComment.getContent().stream()
                    .flatMap(parent -> parent.getChildren().stream())
                    .map(CommentEntity::getCommentId)
                    .collect(Collectors.toList());
            log.debug("자식 댓글 id {}", childrenId);

            // 자식의 대댓글을 가져오기 위해서 조회
            List<CommentEntity> findCommentOfChild = commentRepository.findChildOfChildren(childrenId);

            return findParentComment.map(parent -> {
                // 조회한 부모 id, 없으면 0L 설정
                Long parentCommentId = (parent.getParent() != null) ? parent.getParent().getCommentId() : 0L;
                // ResponseCommentDTO로 변환
                ResponseCommentDTO commentDTO = ResponseCommentDTO.changeDTO(
                        parent,
                        parent.getMember().getNickName(),
                        parentCommentId,
                        findCommentOfChild);
                log.debug("댓글 리스트 반환 값 확인 {}", commentDTO);
                return commentDTO;
            });
        } catch (Exception e) {
            log.warn("댓글 페이징 조회 실패: {}", e.getMessage());
            throw new CommentException("댓글을 페이징 처리해서 가져오는데 실패했습니다.");
        }
    }
}
