package com.chess.game.application.dto.move;

import com.chess.game.util.enums.PieceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveResponseDTO {
    private Long id;
    private Long gameId;
    private Long player;
    private int moveNumber;
    private String fromSquare;
    private String toSquare;
    private PieceType piece;
    private String san;
    private String fen;
}
