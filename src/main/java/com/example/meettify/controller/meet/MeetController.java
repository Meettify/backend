package com.example.meettify.controller.meet;


import com.example.meettify.dto.meet.*;
import com.example.meettify.dto.meetBoard.MeetBoardSummaryDTO;
import com.example.meettify.service.meet.MeetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;




@RestController
@Log4j2
@RequestMapping("/api/v1/meets")
@RequiredArgsConstructor
public class MeetController implements  MeetControllerDocs{
    private final MeetService meetService;

    //모임 리스트 보기
    @Override
    @GetMapping
    public ResponseEntity<?> getList(Pageable pageable, MeetSearchCondition condition,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("condition : " + condition);
            Page<MeetSummaryDTO> meets = meetService.meetsSearch(condition, pageable,userDetails.getUsername());
                    log.info("상품 조회 {}", meets);


            String email = (userDetails != null) ? userDetails.getUsername() : null;

            Map<String, Object> response = new HashMap<>();
            //현재 페이지의 아이템 목록
            response.put("meets", meets.getContent());
            //현재 페이지 번호
            // 현재 페이지 번호
            response.put("nowPageNumber", meets.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", meets.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", meets.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", meets.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", meets.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", meets.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", meets.isLast());


            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error fetching meet list", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 모임 리스트 요청입니다");
        }
    }

    //모임 상세 정보
    @Override
    @GetMapping("{meetId}")
    public ResponseEntity<?> getDetail(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = (userDetails != null) ? userDetails.getUsername() : null;
            MeetRole meetRole = (email != null) ? meetService.getMeetRole(meetId, email) : MeetRole.OUTSIDER;  //

            //권한 정보 가져오기
            MeetPermissionDTO meetPermissionDTO = meetService.getPermission(email, meetId);

            // 모임 디테일 정보를 가져온다.
            MeetDetailDTO meetDetailDTO = meetService.getMeetDetail(meetId);

            List<MeetBoardSummaryDTO> meetBoardSummaryDTO = meetService.getMeetBoardSummaryList(meetId);

            return ResponseEntity.status(HttpStatus.OK).body(MeetDetailInfoResponseDTO.builder()
                    .meetDetailDTO(meetDetailDTO)
                    .meetBoardSummaryDTOList(meetBoardSummaryDTO)
                    .meetId(meetId)
                    .meetPermissionDTO(meetPermissionDTO)
                    .meetRole(meetRole)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 상세조회 실패 : "+e.getMessage());
        }
    }


    //모임 권한 정보 가져오기
    @Override
    @GetMapping("/{meetId}/role")
    public ResponseEntity<?> getMeetRole(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (Objects.isNull(userDetails) ||"".equals(userDetails.getUsername())|| userDetails.getUsername() == null ) {
                ResponseEntity.status(HttpStatus.OK).body(MeetRole.OUTSIDER);
            }

            MeetRole response = meetService.getMeetRole(meetId, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 모임 권한 조회입니다.");
        }
    }

    //모임 가입 회원 리스트 보기
    @Override
    @GetMapping("/{meetId}/members")
    public ResponseEntity<?> getMeetMemberList(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            //해당 모임의 관리자가 아니라면 잘못된 요청임
            if( !(meetService.getMeetRole(meetId,userDetails.getUsername()) == MeetRole.ADMIN)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 모임 리스트 조회입니다.");
            }
            List<ResponseMeetMemberDTO> meetMemberList = meetService.getMeetMemberList(meetId);
            return ResponseEntity.status(HttpStatus.OK).body(meetMemberList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 모임 리스트 조회입니다.");
        }
    }

    //가입한 모임 리스트 보기
    @Override
    @GetMapping("/myMeet")
    public ResponseEntity<?> getMyMeet(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 정보의 모임 가입 리스트 조회입니다.");
            }
            List<MyMeetResponseDTO> meetResponseDTOS = meetService.getMyMeet(email);
            return ResponseEntity.status(HttpStatus.OK).body(meetResponseDTOS);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 가입한 모임 리스트 조회입니다.");
        }
    }

    //관리자 모임 회원 Role변경하기
    @Override
    @PutMapping("/admin/{meetId}/{meetMemberId}")
    public ResponseEntity<?> updateMeetMemberRole(@PathVariable Long meetId,
                                                  @PathVariable Long meetMemberId,
                                                  @RequestBody @Valid UpdateRoleRequestDTO request, // DTO 사용
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 관리자 권한 확인 로직 추가
            if (!(meetService.getMeetRole(meetId, userDetails.getUsername()) == MeetRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("모임 권한 변경 불가 조회입니다.");
            }
            // 회원 Role 업데이트
            MeetRole updatedRole = meetService.updateRole(meetMemberId, request.getNewRole());
            return ResponseEntity.status(HttpStatus.OK).body(updatedRole.name());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 Role 값입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 모임 회원에 대한 접근입니다.");
        }
    }


    //마이페이지에서 모임 탈퇴하는 API
    @Override
    @PutMapping("/{meetId}/{meetMemberId}")
    public ResponseEntity<?> leaveMeet(@PathVariable Long meetId,
                                       @PathVariable Long meetMemberId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {

            String email = userDetails.getUsername();
            // 관리자면 탈퇴 못 하는 로직 추가
            MeetRole role= meetService.getMeetRole(meetId, email);
            if ((role == MeetRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권리자는 모임 휴먼상태로 변경 불가능합니다.");
            }

            MeetRole UpdatedRole = meetService.updateRole(meetMemberId, MeetRole.DORMANT);
            return ResponseEntity.status(HttpStatus.OK).body(UpdatedRole.name());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("모임 탈퇴 중 오류가 발생했습니다." +e.getMessage());
        }
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // BindingResult 타입의 매개변수를 지정하면 BindingResult 매개 변수가 입력값 검증 예외를 처리한다.
    public ResponseEntity<?> makeMeet(@Valid @RequestPart("meet") RequestMeetDTO meet,
                                      @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal UserDetails userDetails) {

        try {
            // 입력값 검증 예외가 발생하면 예외 메세지를 출력
            if (bindingResult.hasErrors()) {
                log.error("binding error: {}", bindingResult.getAllErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
            }
            String email = userDetails.getUsername();
            MeetServiceDTO meetServiceDTO = MeetServiceDTO.makeServiceDTO(meet,images);
            ResponseMeetDTO response = meetService.makeMeet(meetServiceDTO, email);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @Override
    @DeleteMapping("/{meetId}")
    public ResponseEntity<?> delete(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String email = userDetails.getUsername();
            log.info("email : " + email);
            String response = meetService.removeMeet(meetId, email);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //모임 권한 여부 확인
    @Override
    @GetMapping("/{meetId}/members/edit-permission")
    public ResponseEntity<?> checkEditPermission(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            boolean hasPermission = meetService.checkEditPermission(meetId, email); // 수정 권한 체크 로직

            if (hasPermission) {
                return ResponseEntity.ok().body("권한 있음");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //모임 변경하기
    @Override
    @PutMapping(value ="/{meetId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateMeet(@PathVariable Long meetId,
                                        @Valid @RequestPart("updateMeetDTO")UpdateMeetDTO updateMeetDTO,
                                        @RequestPart(value = "images", required = false) List<MultipartFile> newImages,
                                        @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String email = userDetails.getUsername();
            //권한체크
            boolean hasPermission = meetService.checkEditPermission(meetId, email);
            if (hasPermission) {
                // ServiceDTODTO 바꾸는 로직
                UpdateMeetServiceDTO updateMeetServiceDTO = UpdateMeetServiceDTO.makeServiceDTO(meetId,updateMeetDTO);
                // 응답ServiceDTO 받기
                ResponseMeetDTO response = meetService.update(updateMeetServiceDTO, newImages);
                //반환하기
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음");
    }


    //가입 신청하는 기능
    @Override
    @PostMapping("/{meetId}/members")
    public ResponseEntity<?> applyToJoinMeet(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 1. 로그인된 유저인지 확인 (AuthenticationPrincipal로 로그인된 유저 정보 가져옴)
            String userEmail = userDetails.getUsername();

            // 2. 이미 모임에 가입된 회원인지 확인
            boolean alreadyMember = meetService.isAlreadyMember(meetId, userEmail);
            if (alreadyMember) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 가입된 회원입니다.");
            }

            // 3. 회원 가입 신청 처리
            meetService.applyToJoinMeet(meetId, userEmail);
            return ResponseEntity.ok().body("가입 신청이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("오류 발생: " + e.getMessage());
        }
    }
}
