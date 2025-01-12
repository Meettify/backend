package com.example.meettify.service.notice;

import com.example.meettify.config.metric.TimeTrace;
import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseNoticeDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.notice.NoticeEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.repository.jpa.member.MemberRepository;
import com.example.meettify.repository.jpa.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @TimeTrace
    public ResponseNoticeDTO saveBoard(CreateServiceDTO notice, String adminEmail) {
        try {
            MemberEntity findAdmin = memberRepository.findByMemberEmail(adminEmail);
            NoticeEntity noticeEntity = NoticeEntity.changeEntity(notice, findAdmin);
            NoticeEntity saveNotice = noticeRepository.save(noticeEntity);
            return ResponseNoticeDTO.changeNotice(saveNotice);
        } catch (Exception e) {
            log.error("공지사항 등록하는데 실패했습니다. {}", e.getMessage());
            throw new BoardException("공지사항 등록하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 공지 수정
    @Override
    @TimeTrace
    public ResponseNoticeDTO updateBoard(Long noticeId, UpdateServiceDTO notice) {
        try {
            NoticeEntity findNotice = noticeRepository.findById(noticeId)
                    .orElseThrow(() -> new BoardException("공지사항이 존재하지 않습니다."));
            // 공지 수정
            findNotice.updateNotice(notice);
            NoticeEntity saveNotice = noticeRepository.save(findNotice);
            return ResponseNoticeDTO.changeNotice(saveNotice);
        } catch (Exception e) {
            log.error("공지사항 수정하는데 실패했습니다. {}", e.getMessage());
            throw new BoardException("공지사항 수정하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 공지 상세 페이지
    @Override
    @TimeTrace
    @Transactional(readOnly = true)
    public ResponseNoticeDTO getNotice(Long noticeId) {
        try {
            NoticeEntity findNotice = noticeRepository.findById(noticeId)
                    .orElseThrow(() -> new BoardException("공지사항이 존재 하지 않습니다."));
            ResponseNoticeDTO responseCommentDTO = ResponseNoticeDTO.changeNotice(findNotice);
            log.info("responseBoardDTO: {}", responseCommentDTO);
            return responseCommentDTO;
        } catch (Exception e) {
            throw new BoardException("공지사항 조회하는데 실패했습니다. " + e.getMessage());
        }
    }

    // 공지사항 삭제
    @Override
    @TimeTrace
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

    // 공지사항 페이징
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public Page<ResponseNoticeDTO> getAllNotice(Pageable pageable) {
        try {
            Page<NoticeEntity> findAllNotice = noticeRepository.findAll(pageable);
            log.info("조회된 공지사항 수 : {}", findAllNotice.getTotalElements());
            log.info("조회된 공지사항 : {}", findAllNotice);
            return findAllNotice.map(ResponseNoticeDTO::changeNotice);
        } catch (Exception e) {
            throw new BoardException("공지사항을 조회하는데 실패했습니다. : " + e.getMessage());
        }
    }
}
