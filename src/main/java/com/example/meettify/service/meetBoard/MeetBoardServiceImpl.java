package com.example.meettify.service.meetBoard;

import com.example.meettify.config.redis.RedisViewCountConfig;
import com.example.meettify.config.redis.cookie.CookieUtils;
import com.example.meettify.config.s3.S3ImageUploadService;
import com.example.meettify.dto.meet.MeetRole;
import com.example.meettify.dto.meetBoard.*;
import com.example.meettify.dto.member.role.UserRole;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.meet.MeetMemberEntity;
import com.example.meettify.entity.meetBoard.MeetBoardCommentEntity;
import com.example.meettify.entity.meetBoard.MeetBoardEntity;
import com.example.meettify.entity.meetBoard.MeetBoardImageEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.meetBoard.MeetBoardException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.jpa.meet.MeetMemberRepository;
import com.example.meettify.repository.jpa.meet.MeetRepository;
import com.example.meettify.repository.jpa.meetBoard.MeetBoardCommentRepository;
import com.example.meettify.repository.jpa.meetBoard.MeetBoardImageRepository;
import com.example.meettify.repository.jpa.meetBoard.MeetBoardRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 *   worker : 조영흔, 유요한
 *   work   : 모임 게시판 서비스 기능 구현
 *   date   : 2024/09/26
 * */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MeetBoardServiceImpl implements MeetBoardService {
    private final MeetBoardCommentRepository meetBoardCommentRepository;
    private final MeetBoardRepository meetBoardRepository;
    private final MeetBoardImageRepository meetBoardImageRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final MemberRepository memberRepository;
    private final S3ImageUploadService s3ImageUploadService;  // S3 서비스 추가
    private final RedisViewCountConfig redisViewCountConfig;

    @Transactional(readOnly = true)
    public Page<MeetBoardSummaryDTO> getPagedList(Long meetId, Pageable pageable) {
        // meetId에 맞는 게시글 리스트를 페이징 처리하여 가져옴
        Page<MeetBoardEntity> meetBoardPage = meetBoardRepository.findByMeetIdWithMember(meetId, pageable);

        // 레디스에서 조회수 조회
        countRedisView(meetBoardPage);

        // 엔티티를 DTO로 변환하여 반환
        return meetBoardPage.map(MeetBoardSummaryDTO::changeDTO);
    }

    private void countRedisView(Page<MeetBoardEntity> meetBoardPage) {
        // 각 커뮤니티 게시글에 대해 Redis 조회수를 가져와서 합산
        meetBoardPage.forEach(meetBoard -> {
            Integer redisViewCount = redisViewCountConfig.getViewCount("meetBoard:view:" + meetBoard.getMeetBoardId());
            int totalViewCount = meetBoard.getViewCount() + (redisViewCount != null ? redisViewCount : 0);
            meetBoard.setViewCount(totalViewCount);  // or update DTO to reflect this view count
        });
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
@Transactional(readOnly = true)
public MeetBoardDetailsDTO getDetails(String email,
                                      Long meetBoardId,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      PageRequest pageRequest) {
    try {
        // 조회수 중복 방지
        String viewCountCookieValue = CookieUtils.getViewCountCookieValue(request, response);
        log.debug("viewCountCookieValue {}", viewCountCookieValue);

        // 레디스 체크하고 조회수 증가
        if(!redisViewCountConfig.isExistInRedis(viewCountCookieValue, meetBoardId)) {
            increaseViewCountAsync("meetBoard:view:"+meetBoardId, meetBoardId);
            redisViewCountConfig.addToSet(viewCountCookieValue, meetBoardId);
        }

        // 게시글과 이미지를 조인하여 가져옴
        MeetBoardEntity meetBoardEntity = meetBoardRepository.findByMeetBoardId(meetBoardId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다. 게시글 ID: " + meetBoardId));

        // 레디스에서 조회수 가져옴
        Integer viewCount = redisViewCountConfig.getViewCount("meetBoard:view:" + meetBoardId);
        log.debug("레디스에 오른 조회수 확인 {}", viewCount);

        // 조회수 합산
        int totalViewCount = meetBoardEntity.getViewCount() + (viewCount != null ? viewCount : 0);
        log.debug("조회수 합산 {}", totalViewCount);

        // 게시글을 무한 스크롤하기 위해서 부모 댓글 조회
        Slice<MeetBoardCommentEntity> findParentCommentWithChild =
                meetBoardCommentRepository.findCommentByMeetBoardId(meetBoardEntity.getMeetBoardId(), pageRequest);

        // 자식 댓글에 대댓글이 달려있는 경우를 위해서 id를 추츨
        List<Long> childrenId = findParentCommentWithChild.stream()
                .flatMap(parent -> parent.getReplies().stream())
                .map(MeetBoardCommentEntity::getCommentId)
                .collect(Collectors.toList());

        // 자식 댓글의 대댓글을 가져옴
        List<MeetBoardCommentEntity> childComment = meetBoardCommentRepository.findChildComment(childrenId);

        // 유저 조회
        MemberEntity member = memberRepository.findByMemberEmail(email);
        // 모임 회원 정보 조회
        MeetMemberEntity meetMember = meetMemberRepository.findByEmailAndMeetId(member.getMemberEmail(), meetBoardEntity.getMeetEntity().getMeetId())
                .orElseThrow();


        // 자식 댓글 ID - 그 자식들의 대댓글 리스트 매핑
        Map<Long, List<ResponseMeetBoardCommentDTO>> childMap = childComment.stream()
                .map(e -> ResponseMeetBoardCommentDTO.changeDTO(e, resolvePermission(member, meetMember, e)))
                .collect(Collectors.groupingBy(ResponseMeetBoardCommentDTO::getParentComment));

        // 부모 댓글을 DTO로 만들고, 그 replies도 DTO로 바꾼 뒤 재귀적으로 children에 붙이기
        Slice<ResponseMeetBoardCommentDTO> parentCommentDTO = findParentCommentWithChild.map(parent -> {
            ResponseMeetBoardCommentDTO parentDTO =
                    ResponseMeetBoardCommentDTO.changeDTO(
                            parent,
                            resolvePermission(member, meetMember, parent));
            List<ResponseMeetBoardCommentDTO> children = parent.getReplies().stream()
                    .map(child -> {
                        ResponseMeetBoardCommentDTO childDTO =
                                ResponseMeetBoardCommentDTO.changeDTO(
                                        child,
                                        resolvePermission(member, meetMember, child));
                        // 자식의 자식 댓글이 있다면 붙임
                        List<ResponseMeetBoardCommentDTO> grandChildren = childMap.getOrDefault(child.getCommentId(), List.of());
                        childDTO.getChildren().addAll(grandChildren);
                        return childDTO;
                    }).toList();
            parentDTO.getChildren().addAll(children);
            return parentDTO;
        });

        // 빌더 패턴을 사용한 정적 메서드로 DTO 생성 후 반환
        return MeetBoardDetailsDTO.changeDTO(meetBoardEntity, parentCommentDTO, totalViewCount);
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

    // 이 메서드는 Redis에 비동기적으로 조회수를 증가시킵니다.
    // 이를 통해 사용자 요청 처리 시간에 영향을 주지 않고, 조회수를 빠르게 업데이트할 수 있습니다.
    @Async
    public void increaseViewCountAsync(String viewCountKey, Long meetBoardId) {
        try {
            redisViewCountConfig.increaseViewCount(viewCountKey, meetBoardId);
        } catch (Exception e) {
            log.warn("Error increasing view count in Redis for key {}: {}", viewCountKey, e.getMessage());
        }
    }

    // 접속 유저의 댓글 권한 처리
    private MeetBoardCommentPermissionDTO resolvePermission(MemberEntity member,
                                                            MeetMemberEntity meetMember,
                                                            MeetBoardCommentEntity comment) {
        if(member.getMemberEmail().equals(comment.getMemberEntity().getMemberEmail())) {
            return MeetBoardCommentPermissionDTO.of(true, true);    // 본인 → 수정/삭제 가능
        }

        if ((meetMember != null && meetMember.getMeetRole() == MeetRole.ADMIN )
        || member.getMemberRole() == UserRole.ADMIN) {
            return MeetBoardCommentPermissionDTO.of(false, true);   // 관리자 → 삭제만 가능
        }
        return MeetBoardCommentPermissionDTO.of(false, false);      // 일반 사용자
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
                        (oriFileName, uploadFileName, uploadFilePath, uploadFileUrl) ->
                                MeetBoardImageEntity.builder()
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
                        img ->s3ImageUploadService.deleteFile(img.getUploadFilePath(), img.getUploadFileName())
                );
                // 이미지 경로 가져와서 삭제 (이미지 삭제 로직은 그대로 유지)
                meetBoardRepository.deleteById(meetBoardId);
                redisViewCountConfig.deleteCount("meetBoard:view:" + meetBoard);
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

            //1. MeetBoardEntity를 조회하며, 없으면 예외 처리
            MeetBoardEntity findBoard = meetBoardRepository.findById(updateMeetBoardServiceDTO.getMeetBoardId())
                    .orElseThrow(() -> new EntityNotFoundException("변경 대상 엔티티가 존재하지 않습니다."));

            //2. 업데이트할 모임 게시판 정보 적용
            findBoard.updateMeetBoard(updateMeetBoardServiceDTO);

            // 3. 유지하지 않을 이미지들 삭제
            if (findBoard.getMeetBoardImages() != null && !findBoard.getMeetBoardImages().isEmpty()) {
                List<String> existingImgUrls = updateMeetBoardServiceDTO.getImagesUrl();
                List<MeetBoardImageEntity> imagesToDelete = findBoard.getMeetBoardImages().stream()
                        .filter(img -> !existingImgUrls.contains(img.getUploadFileUrl()))
                        .toList();

                if (!imagesToDelete.isEmpty()) {
                    imagesToDelete.forEach(image -> {
                        // S3에서 이미지 삭제
                        s3ImageUploadService.deleteFile(image.getUploadFilePath(), image.getUploadFileName());
                        // 이미지 엔티티 삭제
                        meetBoardImageRepository.delete(image);
                    });
                    findBoard.getMeetBoardImages().removeAll(imagesToDelete); // 엔티티에서 삭제
                }

            }

            // 4. 신규 이미지 등록
            if (updateMeetBoardServiceDTO.getNewImages() != null && !updateMeetBoardServiceDTO.getNewImages().isEmpty()) {
                List<MeetBoardImageEntity> newImageEntities = uploadMeetBoardImages(updateMeetBoardServiceDTO.getNewImages(), findBoard);
                findBoard.getMeetBoardImages().addAll(newImageEntities);
                meetBoardImageRepository.saveAll(newImageEntities);
            }

            // 5. 최종적으로 변경된 엔티티 저장
            meetBoardRepository.save(findBoard);

            return ResponseMeetBoardDTO.changeDTO(findBoard);

        } catch (EntityNotFoundException e) {
            throw new Exception("엔티티가 존재하지 않음: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new Exception("잘못된 인자: " + e.getMessage());
        } catch (Exception e) {
            // 기타 모든 예외에 대한 처리
            throw new Exception("업데이트 도중 예외 발생: " + e.getMessage());
        }
    }


    private List<MeetBoardImageEntity> uploadMeetBoardImages(List<MultipartFile> files, MeetBoardEntity savedBoard) throws  java.io.IOException {
        return s3ImageUploadService.upload("meetImages", files, (oriFileName, uploadFileName, uploadFilePath, uploadFileUrl) ->
                MeetBoardImageEntity.builder()
                        .meetBoardEntity(savedBoard)
                        .oriFileName(oriFileName)
                        .uploadFileName(uploadFileName)
                        .uploadFilePath(uploadFilePath)
                        .uploadFileUrl(uploadFileUrl)
                        .build()
        );
    }


}