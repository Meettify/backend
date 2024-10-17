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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            CommunityEntity findCommunity = communityRepository.findById(communityId).orElseThrow(() -> new BoardException("커뮤니티가 존재하지 않습니다."));
            // 댓글 생성하기 위한 엔티티
            CommentEntity commentEntity = CommentEntity.saveComment(comment, findMember, findCommunity);
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
}
