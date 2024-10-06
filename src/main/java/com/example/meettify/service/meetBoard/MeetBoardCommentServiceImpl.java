package com.example.meettify.service.meetBoard;


import com.example.meettify.dto.meet.MeetRole;
import com.example.meettify.dto.meetBoard.MeetBoardCommentServiceDTO;
import com.example.meettify.dto.meetBoard.ResponseMeetBoardCommentDTO;
import com.example.meettify.entity.meet.MeetEntity;
import com.example.meettify.entity.meet.MeetMemberEntity;
import com.example.meettify.entity.meetBoard.MeetBoardCommentEntity;
import com.example.meettify.entity.meetBoard.MeetBoardEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.meet.MeetException;
import com.example.meettify.exception.meetBoardComment.MeetBoardCommentException;
import com.example.meettify.repository.meet.MeetMemberRepository;
import com.example.meettify.repository.meet.MeetRepository;
import com.example.meettify.repository.meetBoard.MeetBoardCommentRepository;
import com.example.meettify.repository.meetBoard.MeetBoardRepository;
import com.example.meettify.repository.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 *   worker : 조영흔
 *   work   : 모임 게시판 댓글 서비스 기능 구현
 *   date   : 2024/10/04
 * */
@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class MeetBoardCommentServiceImpl implements MeetBoardCommentService {
    private final MemberRepository memberRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final MeetRepository meetRepository;
    private final MeetBoardRepository meetBoardRepository;
    private final MeetBoardCommentRepository meetBoardCommentRepository;



    @Override
    public ResponseMeetBoardCommentDTO postComment(String email, MeetBoardCommentServiceDTO meetBoardCommentServiceDTO) {
        try {
        // 1. 권한 및 작성자 정보 한번에 조회
        MeetMemberEntity meetMemberEntity = meetMemberRepository.findByEmailAndMeetId(email, meetBoardCommentServiceDTO.getMeetId())
                .orElseThrow(() -> new EntityNotFoundException("모임에 접근 권한이 없습니다"));

        // 2. 게시글이 존재하는지 확인
        MeetBoardEntity meetBoard = meetBoardRepository.findById(meetBoardCommentServiceDTO.getMeetBoardId())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다"));

        // 3. 부모 댓글 존재 여부 확인
        MeetBoardCommentEntity parentComment = null;
        if (meetBoardCommentServiceDTO.getParentComment() != null && meetBoardCommentServiceDTO.getParentComment() > 0) {
            parentComment = meetBoardCommentRepository.findById(meetBoardCommentServiceDTO.getParentComment())
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글이 존재하지 않습니다"));
        }

        // 4. 댓글 생성 및 저장
        MeetBoardCommentEntity newComment = MeetBoardCommentEntity.postMeetBoardComment(
                meetBoardCommentServiceDTO, meetMemberEntity.getMemberEntity(), meetBoard, parentComment);

        // 5. 댓글을 데이터베이스에 저장
        MeetBoardCommentEntity savedComment = meetBoardCommentRepository.save(newComment);

        // 6. 저장된 댓글을 Response DTO로 변환하여 반환
        return ResponseMeetBoardCommentDTO.changeDTO(savedComment);
    } catch (EntityNotFoundException e) {
        throw new MeetBoardCommentException(e.getMessage());
    }

}

    @Override
    public String deleteComment(Long meetBoardCommentId) {
        try {
            MeetBoardCommentEntity findComment = meetBoardCommentRepository.findById(meetBoardCommentId).orElseThrow(() -> new EntityNotFoundException("잘못된 삭제 요청입니다."));
            meetBoardCommentRepository.delete(findComment);
            return "모임 게시판 댓글을 삭제했습니다.";
        } catch (Exception e) {
            throw new MeetBoardCommentException(e.getMessage());
        }
    }

    @Override
    public boolean isEditable(String email, Long meetBoardCommentId) {
        try {
            // 1. 댓글을 찾아서 작성자 확인
            MeetBoardCommentEntity comment = meetBoardCommentRepository.findById(meetBoardCommentId)
                    .orElseThrow(() -> new MeetException("댓글이 없습니다."));

            // 2. 작성자 정보 확인 (email과 MemberEntity의 email을 비교)
            boolean isAuthor = comment.getMemberEntity().getMemberEmail().equals(email);

            // 3. MeetMemberEntity에서 관리자인지 확인 (MeetBoardEntity -> MeetEntity -> MeetMemberEntity)
            MeetEntity meet = comment.getMeetBoardEntity().getMeetEntity();
            MeetMemberEntity meetMember = meetMemberRepository.findByEmailAndMeetId(email, meet.getMeetId())
                    .orElseThrow(() -> new MeetException("삭제 요청한 회원은 멤버가 아닙니다."));

            boolean isAdmin = meetMember.getMeetRole().equals(MeetRole.ADMIN);

            // 4. 작성자이거나 관리자인 경우 true 반환
            return isAuthor || isAdmin;

        } catch (Exception e) {
            throw new MeetBoardCommentException(e.getMessage());
        }
    }
}
