package com.example.meettify.entity.member;

/*
 *   worker  : 유요한
 *   work    : 추방된 유저에 관한 테이블을 생성해주는 엔티티 클래스
 *   date    : 2024/12/16
 * */

import com.example.meettify.config.auditing.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "ban_members")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BannedMemberEntity extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String memberEmail;

    public static BannedMemberEntity create(String memberEmail) {
        return BannedMemberEntity.builder().memberEmail(memberEmail).build();
    }
}
