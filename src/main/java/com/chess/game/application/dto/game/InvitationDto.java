package com.chess.game.application.dto.game;

import com.chess.game.util.enums.Invitation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationDto {
    private Long fromUserId;
    private String fromUsername;
    private Long toUserId;
    private String toUsername;
    private Invitation status;
    private String code;
}
