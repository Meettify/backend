package com.example.meettify.entity.meetBoard;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.meetBoard.MeetBoardServiceDTO;
import com.example.meettify.dto.meetBoard.UpdateMeetBoardServiceDTO;
import com.example.meettify.entity.meet.MeetEntity;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 *   worker  : 조영흔
 *   work    : 모임 게시물 관련 정보를 저장하기 위한 엔티티 클래스
 *   date    : 2024/09/19
 * */
@Entity(name = "meetBoards")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MeetBoardEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meetBoard_id")
    private Long meetBoardId;

    @Column(name="meetBoard_title", nullable = false)
    private String meetBoardTitle;

    @Column(name="meetBoard_content")
    private String meetBoardContent;

    // 이미지를 빈 리스트로 초기화
    @OneToMany(mappedBy = "meetBoardEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MeetBoardImageEntity> meetBoardImages = new ArrayList<>();

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity memberEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="meet_id")
    private MeetEntity meetEntity;

    @Column(name="meetBoard_postDate")
    private LocalDateTime postDate;

    // 코멘트도 빈 리스트로 초기화
    @OneToMany(mappedBy = "meetBoardEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommentEntity> comments = new ArrayList<>();

    // 게시글 작성 시간 설정
    @PrePersist
    public void prePersist() {
        this.postDate = (this.postDate == null) ? LocalDateTime.now() : this.postDate;
    }

    // 이미지를 추가하는 메서드
    public void addMeetBoardImage(MeetBoardImageEntity imageEntity) {
        this.meetBoardImages.add(imageEntity);
        imageEntity.linkToMeetBoard(this);  // 양방향 연관관계 설정
    }

    // 이미지를 삭제하는 메서드
    public void removeMeetBoardImage(MeetBoardImageEntity imageEntity) {
        this.meetBoardImages.remove(imageEntity);
        imageEntity.unlinkFromMeetBoard();  // 양방향 연관관계 해제
    }

    // 게시글 내용을 업데이트하는 메서드
// 게시글 내용을 업데이트하는 메서드 (DTO 값으로 필드 값 변경)
    public void updateMeetBoard(UpdateMeetBoardServiceDTO updateMeetBoardServiceDTO) {
        // meetBoardTitle과 meetBoardContent는 엔티티 필드로 정의되어 있어야 합니다
        if (updateMeetBoardServiceDTO.getMeetBoardTitle() != null && !updateMeetBoardServiceDTO.getMeetBoardTitle().isEmpty()) {
            this.meetBoardTitle = updateMeetBoardServiceDTO.getMeetBoardTitle();
        }
        if (updateMeetBoardServiceDTO.getMeetBoardContent() != null && !updateMeetBoardServiceDTO.getMeetBoardContent().isEmpty()) {
            this.meetBoardContent = updateMeetBoardServiceDTO.getMeetBoardContent();
        }
    }

    // 게시글을 생성하는 정적 메서드 (빌더 패턴 사용)
    public static MeetBoardEntity postMeetBoard(MeetBoardServiceDTO meetBoardServiceDTO, MemberEntity member, MeetEntity meetEntity){
        return MeetBoardEntity.builder()
                .meetBoardTitle(meetBoardServiceDTO.getMeetBoardTitle())
                .meetBoardContent(meetBoardServiceDTO.getMeetBoardContent())
                .memberEntity(member)  // 작성자 정보 설정
                .meetEntity(meetEntity)  // 모임 정보 설정
                .build();
    }


}
