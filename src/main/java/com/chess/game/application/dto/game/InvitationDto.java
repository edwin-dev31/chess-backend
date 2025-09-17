package com.chess.game.application.dto.game;

import com.chess.game.util.enums.Invitation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationDto {
    private Long gameId;
    private Long fromUserId;
    private Long toUserId;
    private Invitation status;
}
