package com.chess.game.application.dto.game;

import com.chess.game.domain.MoveStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameStatusDTO {
    private MoveStatus status;
    private String message;
    private String winnerName;
    private boolean isGameOver;
}
