package com.example.meettify.entity.comment;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


/*
 *   worker  : 유요한
 *   work    : 댓글 엔티티
 *   date    : 2024/10/17
 * */
@Entity(name = "comments")
@Getter
@ToString(exclude = {"member", "community", "parent", "children"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CommentEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private CommunityEntity community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    @OrderBy("commentId asc ")
    private List<CommentEntity> children = new ArrayList<>();

    // 생성
    public static CommentEntity saveComment(CreateCommentDTO comment,
                                            MemberEntity member,
                                            CommunityEntity community,
                                            CommentEntity parentComment) {
        return CommentEntity.builder()
                .comment(comment.getComment())
                .member(member)
                .community(community)
                .parent(parentComment)
                .build();
    }

    // 수정
    public void updateComment(UpdateCommentDTO comment) {
        this.comment = comment.getComment();
    }


}
