package com.chess.game.application.dto.game;

import com.chess.game.domain.MoveStatus;
import com.chess.game.util.enums.PieceType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoveCreatedResponseDTO {
    private int moveNumber;
    private String fromSquare;
    private String toSquare;
    private String san;
    private String fen;
    private MoveStatus status;
    private String winnerName;
}
