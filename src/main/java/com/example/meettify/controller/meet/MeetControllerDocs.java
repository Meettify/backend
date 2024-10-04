package com.example.meettify.controller.meet;

import com.example.meettify.dto.meet.RequestMeetDTO;
import com.example.meettify.dto.meet.UpdateMeetDTO;
import com.example.meettify.dto.meet.UpdateRoleRequestDTO;
import com.example.meettify.dto.meet.category.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "meet", description = "모임 API")

public interface MeetControllerDocs {
    @Operation(summary = "모임 리스트", description = "모임 데이터 List를 페이징 처리와 함께 제공해주는 기능")
    public ResponseEntity<?> getList(@RequestParam(defaultValue = "0") Long lastId,
                                     @RequestParam(defaultValue = "9") int size
            , @RequestParam(required = false) Category category);

    @Operation(summary = "모임 디테일 정보", description = "모임 디테일 정보와 현재 모임에서 권한 관련 정보를 전달해줘야 한다.")
    public ResponseEntity<?> getDetail(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails);


    @Operation(summary = "모임 만들기", description = "모임 만들어 주는 API, 신규 모임정보와 이미지, 회원 정보가 필요하다.")
    public ResponseEntity<?> makeMeet(@Valid @RequestBody RequestMeetDTO meet, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "모임 내에서 접속 유저의 권한", description = "모임 내의 권한 정보 전달하기 ")
    public ResponseEntity<?> getMeetRole(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "수정 API", description = "모임에 대한 수정을 진행하는 API")
    public ResponseEntity<?> updateMeet(@PathVariable Long meetId,
                                        @Validated @RequestBody UpdateMeetDTO updateMeetDTO,
                                        @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "모임 가입 회원 리스트 보기", description = "모임 가입 회원 리스트 구현")
    public ResponseEntity<?> getMeetMemberList(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "삭제 API", description = "소모임 삭제 API")
    public ResponseEntity<?> delete(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "수정 권한 체크 API", description = "모임 수정 권한을 체크하는 API")
    public ResponseEntity<?> checkEditPermission(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "모임 가입 신청", description = "회원이 특정 모임에 가입 신청하는 API")
    public ResponseEntity<?> applyToJoinMeet(@PathVariable Long meetId, @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "모임 회원 Role 변경하기", description = "모임 회원 Role 변경 구현")
    public ResponseEntity<?> updateMeetMemberRole(@PathVariable Long meetId,
                                                  @PathVariable Long meetMemberId,
                                                  @RequestBody @Valid UpdateRoleRequestDTO request, // DTO 사용
                                                  @AuthenticationPrincipal UserDetails userDetails);


}
