package com.chess.game.presentation.dto.move;

import com.chess.game.presentation.dto.player.PlayerResponseDTO;
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
    private PlayerResponseDTO player;
    private int moveNumber;
    private String fromSquare;
    private String toSquare;
    private String piece;
    private String san;
    private String fen;
    private LocalDateTime createdAt;
}
