package com.example.meettify.service.member;

import com.example.meettify.dto.jwt.TokenDTO;
import com.example.meettify.dto.member.MemberServiceDTO;
import com.example.meettify.dto.member.UpdateMemberServiceDTO;
import com.example.meettify.dto.member.ResponseMemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface MemberService {
    // 회원가입
    ResponseMemberDTO signUp(MemberServiceDTO member);
    // 이메일 중복 체크
    boolean emailCheck(String email);
    // 닉네임 중복 체크
    boolean nickNameCheck(String nickName);
    // 로그인
    TokenDTO login(String email, String password);
    // 수정
    ResponseMemberDTO update(UpdateMemberServiceDTO updateServiceDTO, String email);
    // 회원삭제
    String removeUser(Long memberId, String email);
    // 화원 조회
    ResponseMemberDTO getMember(Long memberId);
    // 전체적인 회원 가져오기 - 관리자 기능
    Page<ResponseMemberDTO> getMembers(Pageable pageable, String email);
    // 유저 수 전체 조회
    Long countMembers();
}
