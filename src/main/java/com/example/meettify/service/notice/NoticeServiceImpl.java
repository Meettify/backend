package com.example.meettify.service.notice;

import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseBoardDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.notice.NoticeEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.repository.member.MemberRepository;
import com.example.meettify.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
@ToString
@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService{
    private final MemberRepository memberRepository;
    private final NoticeRepository noticeRepository;

    // 공지 생셩
    @Override
    public ResponseBoardDTO saveBoard(CreateServiceDTO notice, String adminEmail) {
        try {
            MemberEntity findAdmin = memberRepository.findByMemberEmail(adminEmail);
            NoticeEntity noticeEntity = NoticeEntity.changeEntity(notice, findAdmin);
            NoticeEntity saveNotice = noticeRepository.save(noticeEntity);
            return ResponseBoardDTO.changeNotice(saveNotice);
        } catch (Exception e) {
            log.error("공지사항 등록하는데 실패했습니다. {}", e.getMessage());
            throw new BoardException("공지사항 등록하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 공지 수정
    @Override
    public ResponseBoardDTO updateBoard(Long noticeId, UpdateServiceDTO notice) {
        try {
            NoticeEntity findNotice = noticeRepository.findById(noticeId)
                    .orElseThrow(() -> new BoardException("공지사항이 존재하지 않습니다."));
            // 공지 수정
            findNotice.updateNotice(notice);
            NoticeEntity saveNotice = noticeRepository.save(findNotice);
            return ResponseBoardDTO.changeNotice(saveNotice);
        } catch (Exception e) {
            log.error("공지사항 수정하는데 실패했습니다. {}", e.getMessage());
            throw new BoardException("공지사항 수정하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 공지 상세 페이지
    @Override
    @Transactional(readOnly = true)
    public ResponseBoardDTO getNotice(Long noticeId) {
        try {
            NoticeEntity findNotice = noticeRepository.findById(noticeId)
                    .orElseThrow(() -> new BoardException("공지사항이 존재 하지 않습니다."));
            ResponseBoardDTO responseBoardDTO = ResponseBoardDTO.changeNotice(findNotice);
            log.info("responseBoardDTO: {}", responseBoardDTO);
            return responseBoardDTO;
        } catch (Exception e) {
            throw new BoardException("공지사항 조회하는데 실패했습니다. " + e.getMessage());
        }
    }

    // 공지사항 삭제
    @Override
    public String deleteNotice(Long noticeId) {
        try {
            NoticeEntity findNotice = noticeRepository.findById(noticeId)
                    .orElseThrow(() -> new BoardException("공지사항이 존재 하지 않습니다."));
            if(findNotice != null) {
                noticeRepository.deleteById(noticeId);
                return "공지사항을 삭제하는데 성공했습니다.";
            }
            throw new BoardException("공지사항이 존재하지 않습니다.");
        } catch (Exception e) {
            throw new BoardException("공지사항을 삭제하는데 실패했습니다. : " + e.getMessage());
        }
    }
}