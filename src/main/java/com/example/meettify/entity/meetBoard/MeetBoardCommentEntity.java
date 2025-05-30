package com.example.meettify.entity.meetBoard;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.meetBoard.MeetBoardCommentPermissionDTO;
import com.example.meettify.dto.meetBoard.MeetBoardCommentServiceDTO;
import com.example.meettify.entity.meet.MeetEntity;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "meetBoardComments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MeetBoardCommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meetBoard_id", nullable = false)
    private MeetBoardEntity meetBoardEntity;  // 게시글과의 연관관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;  // 댓글 작성자 정보

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "postDate")
    private LocalDateTime postDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private MeetBoardCommentEntity parentComment;  // 대댓글인 경우 부모 댓글과의 연관관계

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<MeetBoardCommentEntity> replies = new ArrayList<>();  // 대댓글 리스트

    @Embedded
    private MeetBoardCommentPermissionEntity permission;

    private boolean isDeleted = false;

    @PrePersist
    public void prePersist() {
        this.postDate = (this.postDate == null) ? LocalDateTime.now() : this.postDate;
    }

    // 댓글 추가 메소드
    public void addReply(MeetBoardCommentEntity reply) {
        this.replies.add(reply);
        reply.parentComment =this;  // 양방향 연관관계 설정
    }

    // 댓글 수정 메소드
    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public static MeetBoardCommentEntity postMeetBoardComment(MeetBoardCommentServiceDTO meetBoardCommentServiceDTO,
                                                              MemberEntity member,
                                                              MeetBoardEntity meetBoard,
                                                              MeetBoardCommentEntity parentComment) {
        return MeetBoardCommentEntity.builder()
                .parentComment(parentComment)
                .content(meetBoardCommentServiceDTO.getContent())
                .postDate(meetBoardCommentServiceDTO.getPostDate())
                .meetBoardEntity(meetBoard)
                .memberEntity(member)
                .build();
    }

    // 권한 설정 엔티티로 변환
    public static MeetBoardCommentPermissionEntity changePermission(MeetBoardCommentPermissionDTO permissionDTO) {
        return MeetBoardCommentPermissionEntity.builder()
                .canEdit(permissionDTO.isCanEdit())
                .canDelete(permissionDTO.isCanDelete())
                .build();
    }

    // 권한 추가
    public void addPermission(MeetBoardCommentPermissionEntity permission) {
        this.permission = permission;
    }

    // 대댓글이 있을 경우 삭제 표시
    public void changeDelete() {
        this.isDeleted = true;
    }
}