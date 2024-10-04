package com.example.meettify.service.meetBoard;


import com.example.meettify.dto.meetBoard.MeetBoardCommentServiceDTO;
import com.example.meettify.dto.meetBoard.ResponseMeetBoardCommentDTO;
import com.example.meettify.entity.meet.MeetMemberEntity;
import com.example.meettify.entity.meetBoard.MeetBoardCommentEntity;
import com.example.meettify.entity.meetBoard.MeetBoardEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.meet.MeetException;
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
        throw new MeetException(e.getMessage());
    }

}
}
