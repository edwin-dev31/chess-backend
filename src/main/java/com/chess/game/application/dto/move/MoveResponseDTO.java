package com.chess.game.application.dto.move;

import com.chess.game.application.dto.player.PlayerResponseDTO;
import com.chess.game.util.PieceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
