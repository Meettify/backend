package com.example.meettify.entity.meetBoard;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetBoardCommentPermissionEntity {
    private boolean canEdit;
    private boolean canDelete;
}
