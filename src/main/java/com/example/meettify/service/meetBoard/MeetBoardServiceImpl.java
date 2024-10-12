package com.example.meettify.service.meetBoard;

import com.example.meettify.config.s3.S3ImageUploadService;
import com.example.meettify.dto.meet.MeetRole;
import com.example.meettify.dto.meetBoard.*;
import com.example.meettify.dto.member.role.UserRole;
import com.example.meettify.entity.meet.MeetMemberEntity;
import com.example.meettify.entity.meetBoard.MeetBoardEntity;
import com.example.meettify.entity.meetBoard.MeetBoardImageEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.meetBoard.MeetBoardException;
import com.example.meettify.repository.meet.MeetMemberRepository;
import com.example.meettify.repository.meet.MeetRepository;
import com.example.meettify.repository.meetBoard.MeetBoardImageRepository;
import com.example.meettify.repository.meetBoard.MeetBoardRepository;
import com.example.meettify.repository.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 *   worker : 조영흔
 *   work   : 모임 게시판 서비스 기능 구현
 *   date   : 2024/09/26
 * */
@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class MeetBoardServiceImpl implements MeetBoardService {
    private final MeetBoardRepository meetBoardRepository;
    private final MeetBoardImageRepository meetBoardImageRepository;
    private final MeetRepository meetRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final S3ImageUploadService s3ImageUploadService;  // S3 서비스 추가
    private final MeetBoardCommentService meetBoardCommentService;


    public Page<MeetBoardSummaryDTO> getPagedList(Long meetId, Pageable pageable) {
        // meetId에 맞는 게시글 리스트를 페이징 처리하여 가져옴
        Page<MeetBoardEntity> meetBoardPage = meetBoardRepository.findByMeetIdWithMember(meetId, pageable);

        // 엔티티를 DTO로 변환하여 반환
        return meetBoardPage.map(MeetBoardSummaryDTO::changeDTO);
    }

    @Override
    public MeetBoardPermissionDTO getPermission(String email, Long meetBoardId) {
        try {
            // 이메일로 회원 정보 가져옴
            MemberEntity member = memberRepository.findByMemberEmail(email);

            //모임 게시글이 없을 경우에는 예외 처리
            MeetBoardEntity meetBoard = meetBoardRepository.findById(meetBoardId).orElseThrow(()->new EntityNotFoundException("존재 하지 않는 모임 게시글에 대한 조회입니다."));

            MeetMemberEntity meetMember = meetMemberRepository.findByEmailAndMeetId(email, meetBoard.getMeetEntity().getMeetId())
                    .orElseGet(null);

            // 게시글 작성자 인지 확인해서 맞으면  삭제 및 수정 권한 부여
            if (meetBoard.getMemberEntity().equals(member)) {
                return MeetBoardPermissionDTO.of(true, true); // 삭제만 가능
            }

            // 모임 관리자 혹은 싸이트 관리자라면 삭제 권한으 부여
            if (meetMember !=null?  MeetRole.ADMIN == meetMember.getMeetRole()
                    : UserRole.ADMIN == member.getMemberRole()) {
                return MeetBoardPermissionDTO.of(false, true); // 삭제만 가능
            }

            return MeetBoardPermissionDTO.of(false, false); // 삭제만 가능
        } catch (Exception e) {
            throw new MeetBoardException(e.getMessage());
        }
    }


@Override
public MeetBoardDetailsDTO getDetails(String email,Long meetBoardId) {
    try {
        // 게시글과 이미지를 조인하여 가져옴
        MeetBoardEntity meetBoardEntity = meetBoardRepository.findById(meetBoardId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다. 게시글 ID: " + meetBoardId));

        // 댓글 리스트 변환
        List<ResponseMeetBoardCommentDTO> commentDTOs = meetBoardEntity.getComments() != null ?
                meetBoardEntity.getComments().stream()
                        .map(e-> ResponseMeetBoardCommentDTO.changeDTO(e, meetBoardCommentService.getPermission(email,meetBoardId)))
                        .toList() : new ArrayList<>();

        // 빌더 패턴을 사용한 정적 메서드로 DTO 생성 후 반환
        return MeetBoardDetailsDTO.changeDTO(meetBoardEntity,commentDTOs);
    } catch (EntityNotFoundException e) {
        throw new MeetBoardException("게시글을 찾을 수 없습니다: " + meetBoardId);
    } catch (Exception e) {
        // 오류 원인 출력
        System.out.println("오류 메시지: " + e.getMessage());
        System.out.println("오류 원인: " + e.getCause());
        for (StackTraceElement element : e.getStackTrace()) {
            System.out.println(element.toString());
        }
        throw new MeetBoardException("게시글 상세 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
    }
}




    @Override
    public ResponseMeetBoardDTO postBoard(MeetBoardServiceDTO meetBoardServiceDTO, String email) throws Exception {
        try {
            // 1. 이메일로 작성자 정보 조회
            MemberEntity member = memberRepository.findByMemberEmail(email);
            long memberId = member.getMemberId();
            long meetId = meetBoardServiceDTO.getMeetId();

            // 2. 모임에 포스팅할 수 있는 권한이 있는지 확인
            MeetMemberEntity meetMemberEntity = meetMemberRepository.findByEmailAndMeetId(email, meetId)
                    .orElseThrow(() -> new EntityNotFoundException("모임에 접근 권한이 없습니다"));

            // 3. meetBoardEntity 변환 및 작성자와 모임 정보 설정
            MeetBoardEntity meetBoard =  MeetBoardEntity.postMeetBoard(meetBoardServiceDTO, member, meetMemberEntity.getMeetEntity());

            // 4. meetBoardEntity 저장
            MeetBoardEntity savedMeetBoard = meetBoardRepository.save(meetBoard);

            // 5. 이미지가 있을 경우 이미지 리스트 저장
            if (meetBoardServiceDTO.getImagesFile() != null && !meetBoardServiceDTO.getImagesFile().isEmpty()) {
                List<MeetBoardImageEntity> imageEntities = s3ImageUploadService.upload(
                        "meetImages",
                        meetBoardServiceDTO.getImagesFile(),
                        (oriFileName, uploadFileName, uploadFilePath, uploadFileUrl) -> MeetBoardImageEntity.builder()
                                .meetBoardEntity(savedMeetBoard)
                                .oriFileName(oriFileName)
                                .uploadFileName(uploadFileName)
                                .uploadFilePath(uploadFilePath)
                                .uploadFileUrl(uploadFileUrl)
                                .build()
                );

                // 6. 업로드된 이미지들을 DB에 저장 및 meetBoard에 추가
                meetBoardImageRepository.saveAll(imageEntities);
                imageEntities.forEach(savedMeetBoard::addMeetBoardImage);
            }

            // 7. 응답 DTO 생성 및 반환
            return ResponseMeetBoardDTO.changeDTO(savedMeetBoard);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String deleteBoard(Long meetId, Long meetBoardId, String email) throws Exception {
        try {
            MeetBoardEntity meetBoard = meetBoardRepository.findById(meetBoardId).orElseThrow(EntityNotFoundException::new);
            //권한 체크
            if (getPermission(email,meetBoardId).isCanDelete() ) {
                //S3에서 이미지 삭제하는 로직 만들기.
                meetBoard.getMeetBoardImages().forEach(
                        img ->s3ImageUploadService.deleteFile(img.getUploadFilePath(), img.getOriFileName())
                );
                // 이미지 경로 가져와서 삭제 (이미지 삭제 로직은 그대로 유지)
                meetBoardRepository.deleteById(meetBoardId);
                log.info("Successfully deleted meetBoard with id: {}", meetBoardId);
                return "게시물을 삭제했습니다.";
            }
            return "게시물을 삭제할 권한이 없습니다.";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ResponseMeetBoardDTO updateBoardService(UpdateMeetBoardServiceDTO updateMeetBoardServiceDTO, String username) throws Exception {
        try {
            // updateMeetBoardServiceDTO 자체가 Null일 수 있으므로 먼저 검사
            if (updateMeetBoardServiceDTO == null) {
                throw new IllegalArgumentException("업데이트 데이터가 null입니다.");
            }

            // MeetBoardEntity를 조회하며, 없으면 예외 처리
            MeetBoardEntity meetBoardEntity = meetBoardRepository.findById(updateMeetBoardServiceDTO.getMeetBoardId())
                    .orElseThrow(() -> new EntityNotFoundException("변경 대상 엔티티가 존재하지 않습니다."));

            // 엔티티 업데이트
            meetBoardEntity.updateMeetBoard(updateMeetBoardServiceDTO);

            // 이미지를 업데이트하는 로직
            if (updateMeetBoardServiceDTO.getImages() != null && !updateMeetBoardServiceDTO.getImages().isEmpty()) {
                List<MeetBoardImageEntity> imageEntities = s3ImageUploadService.upload(
                        "meetImages",
                        updateMeetBoardServiceDTO.getImages(),
                        (oriFileName, uploadFileName, uploadFilePath, uploadFileUrl) -> MeetBoardImageEntity.builder()
                                .meetBoardEntity(meetBoardEntity)
                                .oriFileName(oriFileName)
                                .uploadFileName(uploadFileName)
                                .uploadFilePath(uploadFilePath)
                                .uploadFileUrl(uploadFileUrl)
                                .build()
                );

                // 기존 이미지 중에서 업데이트되지 않은 이미지를 삭제하는 로직 추가
                // 예: meetBoardEntity에 있는 기존 이미지 목록과 updateMeetBoardServiceDTO.getImages()를 비교해서 삭제
                // 이 부분은 사용자에 맞게 추가적으로 구현해야 함
            }

            // 업데이트된 엔티티를 DTO로 변환해서 반환
            meetBoardRepository.save(meetBoardEntity);
            ResponseMeetBoardDTO responseMeetBoardDTO =   ResponseMeetBoardDTO.changeDTO(meetBoardEntity);
            log.info("response : {}", responseMeetBoardDTO);
            return ResponseMeetBoardDTO.changeDTO(meetBoardEntity);

        } catch (EntityNotFoundException e) {
            throw new Exception("엔티티가 존재하지 않음: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new Exception("잘못된 인자: " + e.getMessage());
        } catch (Exception e) {
            // 기타 모든 예외에 대한 처리
            throw new Exception("업데이트 도중 예외 발생: " + e.getMessage());
        }
    }



}