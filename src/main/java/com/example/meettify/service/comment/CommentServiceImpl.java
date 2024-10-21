package com.example.meettify.service.comment;

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
    public ResponseCommentDTO createComment(Long communityId,
                                            CreateCommentDTO comment,
                                            String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 커뮤니티 조회
            CommunityEntity findCommunity = communityRepository.findById(communityId)
                    .orElseThrow(() -> new BoardException("커뮤니티가 존재하지 않습니다."));

            CommentEntity parentComment = null;
            if (comment.getCommentParentId() != null) {
                // 부모 댓글이 있는 경우 대댓글로 설정
                parentComment = commentRepository.findById(comment.getCommentParentId())
                        .orElseThrow(() -> new CommentException("부모 댓글이 존재하지 않습니다."));
            }

            // 댓글 생성하기 위한 엔티티
            CommentEntity commentEntity = CommentEntity.saveComment(comment, findMember, findCommunity, parentComment);
            // 댓글 디비에 저장
            CommentEntity saveComment = commentRepository.save(commentEntity);
            ResponseCommentDTO response = ResponseCommentDTO.changeDTO(saveComment);
            log.info("반환 댓글 : " + response);
            return response;
        } catch (Exception e) {
            log.error("댓글 생성하는데 실패했습니다. {} ", e.getMessage());
            throw new CommentException("댓글 생성하는데 실패했습니다. : "+ e.getMessage());
        }
    }

    // 뎃글 수정
    @Override
    public ResponseCommentDTO updateComment(Long communityId, Long commentId, UpdateCommentDTO comment, String email) {
        try {
            CommunityEntity findCommunity = communityRepository.findById(commentId)
                    .orElseThrow(() -> new BoardException("커뮤니티 글이 없습니다."));

            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentException("댓글이 없습니다."));
            // 댓글 수정
            findComment.updateComment(comment);
            CommentEntity updateComment = commentRepository.save(findComment);
            ResponseCommentDTO response = ResponseCommentDTO.changeDTO(updateComment);
            log.info("수정된 댓글 {} ", response );
            return response;
        } catch (Exception e) {
            throw new CommentException("댓글 수정하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 댓글 삭제
    @Override
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
    public ResponseCommentDTO getComment(Long commentId) {
        try {
            CommentEntity findComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentException("댓글 조회해씁니다."));
            return ResponseCommentDTO.changeDTO(findComment);
        } catch (Exception e) {
            throw new CommentException("댓글 조회하는데 실패했습니다.");
        }
    }

    // 댓글 페이징처리
    @Override
    @Transactional(readOnly = true)
    public Page<ResponseCommentDTO> getComments(Pageable page, Long communityId) {
        try {
            // 댓글과 대댓글을 모두 가져옴
            Page<CommentEntity> findAllComment = commentRepository.findCommentByCommunityId(communityId, page);

            // 댓글 엔티티를 DTO로 변환
            List<ResponseCommentDTO> responseComments = findAllComment.stream()
                    .map(ResponseCommentDTO::changeDTO)
                    .toList();

            // 중첩 구조로 변환
            List<ResponseCommentDTO> nestedComments = convertNestedStructure(responseComments);

            // 변환된 댓글 구조로 페이지 반환
            return new PageImpl<>(nestedComments, page, findAllComment.getTotalElements());
        } catch (Exception e) {
            throw new CommentException("상품을 페이징 처리해서 가져오는데 실패했습니다.");
        }
    }
    private List<ResponseCommentDTO> convertNestedStructure(List<ResponseCommentDTO> comments) {
        List<ResponseCommentDTO> result = new ArrayList<>();
        Map<Long, ResponseCommentDTO> map = new HashMap<>();

        // 모든 댓글을 순회하며 ID 기준으로 맵에 저장
        comments.forEach(c -> {
            map.put(c.getCommentId(), c);
        });

        // 부모-자식 관계 설정
        comments.forEach(c -> {
            if (c.getParentId() != null) { // Parent ID가 있는 경우 (자식 댓글)
                // 부모 댓글의 자식 목록에 자식 댓글 추가
                ResponseCommentDTO parentComment = map.get(c.getParentId());
                if (parentComment != null) {
                    parentComment.getChildren().add(c);
                }
            } else {
                // 부모 댓글일 경우 최상위 댓글로 추가
                result.add(c);
            }
        });

        return result;
    }
}
