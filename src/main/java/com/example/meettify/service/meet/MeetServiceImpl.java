package com.example.meettify.service.meet;

import com.example.meettify.config.s3.S3ImageUploadService;
import com.example.meettify.dto.meet.*;
import com.example.meettify.dto.meetBoard.MeetBoardSummaryDTO;
import com.example.meettify.dto.member.role.UserRole;
import com.example.meettify.entity.chat_room.ChatRoomEntity;
import com.example.meettify.entity.meet.MeetEntity;
import com.example.meettify.entity.meet.MeetImageEntity;
import com.example.meettify.entity.meet.MeetMemberEntity;
import com.example.meettify.entity.meetBoard.MeetBoardEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.meet.MeetException;
import com.example.meettify.exception.meetBoard.MeetBoardException;
import com.example.meettify.repository.jpa.chat.ChatRoomRepository;
import com.example.meettify.repository.jpa.meet.MeetImageRepository;
import com.example.meettify.repository.jpa.meet.MeetMemberRepository;
import com.example.meettify.repository.jpa.meet.MeetRepository;
import com.example.meettify.repository.jpa.meetBoard.MeetBoardRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/*
 *   worker : 조영흔, 유요한
 *   work   : 서비스 로직 구현
 *   date   : 2024/09/24
 * */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MeetServiceImpl implements MeetService {
    private final ModelMapper modelMapper;
    private final MeetRepository meetRepository;
    private final MeetImageRepository meetImageRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final MemberRepository memberRepository;
    private final MeetBoardRepository meetBoardRepository;
    private final S3ImageUploadService s3ImageUploadService;  // S3 서비스 추가
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ResponseMeetDTO makeMeet(MeetServiceDTO meet, String email) throws IOException, java.io.IOException {
        // 1. MeetServiceDTO에서 MeetEntity로 변환
        MeetEntity meetEntity = modelMapper.map(meet, MeetEntity.class);
        log.info("이미지 확인 {}", meet.getImagesFile());

        // 2. MeetEntity를 저장소에 저장
        MeetEntity savedMeet = meetRepository.save(meetEntity);
        List<MeetImageEntity> imageEntities = new ArrayList<>();

        // 3. 이미지가 있는 경우, S3에 업로드하고 해당 URL을 MeetImageEntity로 저장
        if (meet.getImagesFile() != null && !meet.getImagesFile().isEmpty()) {
            log.info("이미지 저장로직이 실행됩니다.");
            meet.getImagesFile().stream().forEach(e-> System.out.println(e.getName()));
            meet.getImagesFile().stream().forEach(e-> System.out.println(e.getOriginalFilename()));
            // S3에 이미지 업로드 및 DTO 생성 (FileDTOFactory 활용)
            imageEntities = uploadMeetImages(meet.getImagesFile(), savedMeet);
            // 업로드된 이미지들을 DB에 저장
            meetImageRepository.saveAll(imageEntities);
        }

        // 4. 모임 생성자를 ADMIN으로 MeetMemberEntity에 등록
        MemberEntity creator = memberRepository.findByMemberEmail(email);
        MeetMemberEntity meetMemberEntity = MeetMemberEntity.builder()
                .meetEntity(savedMeet)
                .memberEntity(creator)
                .meetRole(MeetRole.ADMIN)
                .joinDate(LocalDateTime.now())
                .build();

        // MeetMemberEntity 저장
        meetMemberRepository.save(meetMemberEntity);


        // 5. 응답 DTO 생성 및 반환
        ResponseMeetDTO  responseMeetDTO = ResponseMeetDTO.changeDTO(savedMeet);

        return responseMeetDTO;
    }

    @Override
    public String removeMeet(Long meetId, String email) {
        try {
            MeetMemberEntity meetMemberEntity = meetMemberRepository.findByEmailAndMeetId(email, meetId)
                    .orElseThrow(() -> new MeetException("Member not part of this meet."));
            if (meetMemberEntity.getMeetRole() == MeetRole.ADMIN) {

                MeetEntity meetEntity = meetRepository.findById(meetId).get();
                // s3에 이미지 삭제
                meetEntity.getMeetImages().forEach(
                        img -> s3ImageUploadService.deleteFile(img.getUploadFilePath(), img.getUploadFileName())
                );

                meetRepository.deleteById(meetId);

                log.info("Successfully deleted meet with id: {}", meetId);
                return "소모임 삭제 완료";
            }
            throw new MeetException("회원ID과 모임 관리자정보가 일치하지 않습니다.");
        } catch (EntityNotFoundException e) {
            throw new MeetException(e.getMessage());
        }
    }

    @Override
    public boolean checkEditPermission(Long meetId, String email) {
        try {
            MeetMemberEntity meetMemberEntity = meetMemberRepository.findByEmailAndMeetId(email, meetId)
                    .orElseThrow(() -> new MeetException("Member not part of this meet."));

            if (meetMemberEntity.getMeetRole() == MeetRole.ADMIN) {
                return true;
            }
        } catch (EntityNotFoundException e) {
            throw new MeetException("권한 관련 정보가 없습니다.");
        }
        return false;
    }

    @Override
    @Transactional
    public ResponseMeetDTO update(UpdateMeetServiceDTO updateMeetServiceDTO, List<MultipartFile> newImages) throws IOException, java.io.IOException {
        // 1. 변경 요청한 모임이 존재하는지 확인
        MeetEntity findMeet = meetRepository.findById(updateMeetServiceDTO.getMeetId())
                .orElseThrow(() -> new MeetException("변경 대상 엔티티가 존재하지 않습니다."));

        // 2. 업데이트할 모임 정보 적용
        findMeet.updateMeet(updateMeetServiceDTO);

        // 3. 유지하지 않을 이미지들 삭제
        if (findMeet.getMeetImages() != null && !findMeet.getMeetImages().isEmpty()) {
            List<String> existingImgUrls = updateMeetServiceDTO.getExistingImageUrls();
            List<MeetImageEntity> imagesToDelete = findMeet.getMeetImages().stream()
                    .filter(img -> !existingImgUrls.contains(img.getUploadFileUrl()))
                    .toList();

            if (!imagesToDelete.isEmpty()) {
                imagesToDelete.forEach(image -> {
                    // S3에서 이미지 삭제
                    s3ImageUploadService.deleteFile(image.getUploadFilePath(), image.getUploadFileName());
                    // 이미지 엔티티 삭제
                    meetImageRepository.delete(image);
                });
                findMeet.getMeetImages().removeAll(imagesToDelete); // 엔티티에서 삭제
            }
        }

        // 4. 신규 이미지 등록
        if (newImages != null && !newImages.isEmpty()) {
            List<MeetImageEntity> newImageEntities = uploadMeetImages(newImages, findMeet);
            findMeet.getMeetImages().addAll(newImageEntities); // 새로운 이미지를 엔티티에 추가
            meetImageRepository.saveAll(newImageEntities); // DB에 이미지 저장
        }

        // 5. 최종적으로 변경된 엔티티 저장
        meetRepository.save(findMeet);

        // 6. 응답 DTO 생성 및 반환
        return ResponseMeetDTO.changeDTO(findMeet);
    }

    // 이미 가입된 회원인지 확인
    @Override
    public boolean isAlreadyMember(Long meetId, String email) {
        MemberEntity member = memberRepository.findByMemberEmail(email);
        MeetEntity meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new EntityNotFoundException("Meet not found"));
        return meetMemberRepository.existsByMeetEntityAndMemberEntity(meet, member);
    }

    // 가입 신청 처리
    @Override
    @Transactional
    public void applyToJoinMeet(Long meetId, String email) {
        MemberEntity member = memberRepository.findByMemberEmail(email);
        MeetEntity meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임이 존재하지 않습니다."));

        // 현재 인원수 구하기
        Long currCount  = meetMemberRepository.findMeetMembersWithMeetAndMember(meetId).stream()
                .filter(e-> e.getMeetRole() == MeetRole.ADMIN || e.getMeetRole() == MeetRole.MEMBER ).count();

        if (currCount >= meet.getMeetMaximum()) {
            throw new MeetException("인원수가 다 차서 더 이상 인원을 받을 수 없습니다. \n현재인원 : "+currCount+" \n맥스인원 : "+meet.getMeetMaximum());
        }

        // MeetMemberEntity에 새로운 가입 요청 저장
        MeetMemberEntity meetMember = MeetMemberEntity.builder()
                .meetEntity(meet)
                .memberEntity(member)
                .meetRole(MeetRole.WAITING) // 일반 회원으로 가입
                .joinDate(LocalDateTime.now())
                .build();

        meetMemberRepository.save(meetMember);
    }

    @Override
    public MeetDetailDTO getMeetDetail(Long meetId) {
        try {
            // 1. meetId로 MeetEntity 조회
            MeetEntity meetEntity = meetRepository.findByIdWithImages(meetId)
                    .orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않음."));

            // 2. MeetEntity를 MeetDetailDTO로 매핑
            MeetDetailDTO responseMeetDTO = MeetDetailDTO.changeDTO(meetEntity);

            return responseMeetDTO;
        }catch (Exception e){
            throw new MeetException(e.getMessage());
        }
    }


    @Override
    public MeetRole getMeetRole(Long meetId, String email) {
        try {
            MeetMemberEntity meetMemberEntity = meetMemberRepository.findByEmailAndMeetId(email, meetId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
            return meetMemberEntity.getMeetRole();
        } catch (Exception e) {
            log.error("error : "+e.getMessage());
            return MeetRole.OUTSIDER;
        }
    }

    // ToDO: fetch JOIN 다시 손 보기
    // 여러 모임을 페이징 처리해서 가져오는 메서드 : 조건 검색 가능
    @Transactional(readOnly = true)
    public Page<MeetSummaryDTO> meetsSearch(MeetSearchCondition condition, Pageable pageable, String email) {
        try {
            // MeetEntity 페이지 가져오기
            Page<MeetEntity> meetPage = meetRepository.meetsSearch(condition, pageable);

            // 사용자 정보를 통해 모임 멤버 ID 목록 조회
            MemberEntity member = memberRepository.findByMemberEmail(email);
            Set<Long> memberMeetIds = (member != null) ? meetMemberRepository.findMeetMemberIdByEmail(email) : Collections.emptySet();
            log.debug("반환값 확인 {}", meetPage.map(meet -> MeetSummaryDTO.changeDTO(meet, memberMeetIds)).getContent());
            return meetPage.map(meet -> MeetSummaryDTO.changeDTO(meet, memberMeetIds));
        } catch (Exception e) {
            log.error("error : " + e.getMessage());
            throw new EntityNotFoundException("모임 조회에 실패하였습니다.\n" + e.getMessage());
        }
    }



    // ToDO: fetch JOIN 다시 손 보기
    @Override
    public List<MeetBoardSummaryDTO>  getMeetBoardSummaryList(Long meetId) {
        try {
            Pageable pageable = PageRequest.of(0, 3);  // 첫 번째 페이지에서 3개의 결과만 가져옴
            List<MeetBoardEntity> recentMeetBoards = meetBoardRepository.findTop3MeetBoardEntitiesByMeetId(meetId, pageable);

            // 게시글이 없을 경우
            if (recentMeetBoards.isEmpty()) {
                return Collections.emptyList();
            }

            return recentMeetBoards.stream().map(board -> MeetBoardSummaryDTO.builder()
                    .meetBoardId(board.getMeetBoardId())
                    .title(board.getMeetBoardTitle())
                    .postDate(board.getPostDate())
                    .nickName(board.getMemberEntity().getNickName())
                    .build()).toList();
        }catch (DataAccessException dae) {
            throw new MeetBoardException("데이터베이스 접근 중 오류가 발생했습니다.");
        } catch (NullPointerException npe) {
            throw new MeetBoardException("필요한 데이터가 누락되었습니다.");
        } catch (Exception e) {
            throw new MeetBoardException("알 수 없는 오류가 발생했습니다.");
        }
    }

    @Override
    public List<ResponseMeetMemberDTO> getMeetMemberList(Long meetId) {

        List<MeetMemberEntity> meetMemberList = meetMemberRepository.findMeetMembersWithMeetAndMember(meetId);
        return meetMemberList.stream().map(ResponseMeetMemberDTO::changeDTO).collect(Collectors.toList());
    }

    // 권한 수정
    @Override
    public MeetRole updateRole(Long meetMemberId, MeetRole meetRole) {

        try {
            MeetMemberEntity findMeetMember = meetMemberRepository.findByMeetMemberId(meetMemberId);
            findMeetMember.updateRole(meetRole);
            MeetRole updatedMeetRole = findMeetMember.getMeetRole();
            ChatRoomEntity findChatRoom = chatRoomRepository.findByMeetId(findMeetMember.getMeetEntity().getMeetId());
            log.debug("채팅방 확인 {}", findChatRoom);
            if (!findChatRoom.getInviteMemberIds().contains(findMeetMember.getMemberEntity().getMemberId())) {
                findChatRoom.getInviteMemberIds().add(findMeetMember.getMemberEntity().getMemberId());
            }
            return updatedMeetRole;
        } catch (EntityNotFoundException e) {
            throw new MeetException("해당 회원이 존재하지 않습니다.");
        } catch (Exception e) {
            throw new MeetException("Role 변경 중 오류가 발생했습니다.");
        }
    }

    @Override
    public MeetPermissionDTO getPermission(String email, Long meetId) {
        try {
            // 이메일로 회원 정보 가져옴
            MemberEntity member = memberRepository.findByMemberEmail(email);

            // 모임에 속하지 않을 수도 있으므로 meetMember는 null일 수 있음
            MeetMemberEntity meetMember = meetMemberRepository.findByEmailAndMeetId(email, meetId).orElse(null);

            // 모임 관리자 권한 확인
            if (meetMember != null && MeetRole.ADMIN == meetMember.getMeetRole()) {
                return MeetPermissionDTO.of(true, true); // 수정 및 삭제 가능
            }

            // 전체 사이트 ADMIN 권한 확인
            if (member != null && UserRole.ADMIN == member.getMemberRole()) {
                return MeetPermissionDTO.of(false, true); // 삭제만 가능
            }

            // 권한이 없는 경우
            return MeetPermissionDTO.of(false, false); // 수정 및 삭제 불가
        } catch (Exception e) {
            throw new MeetException("권한 관련 조회 중 에러 발생: " + e.getMessage());
        }
    }

    //내가 가입한 모임 리스트 구현
    @Override
    public Page<MyMeetResponseDTO> getMyMeet(String email, Pageable page) {
        try {
            Page<MeetMemberEntity> findMeetList = meetMemberRepository.findMeetsByMemberName(email, page);

            return findMeetList.map(MyMeetResponseDTO::changeDTO);

        } catch (Exception e) {
            throw new MeetException("가입한 모임 리스트 조회 중 에러 발생: " + e.getMessage());
        }
    }

    private List<MeetImageEntity> uploadMeetImages(List<MultipartFile> files, MeetEntity savedMeet) throws  java.io.IOException {
        return s3ImageUploadService.upload("meetImages", files, (oriFileName, uploadFileName, uploadFilePath, uploadFileUrl) ->
                MeetImageEntity.builder()
                        .meetEntity(savedMeet)
                        .oriFileName(oriFileName)
                        .uploadFileName(uploadFileName)
                        .uploadFilePath(uploadFilePath)
                        .uploadFileUrl(uploadFileUrl)
                        .build()
        );
    }


}
