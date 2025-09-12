package com.chess.game.presentation.dto.game;

import com.chess.game.presentation.dto.player.PlayerResponseDTO;
import com.chess.game.util.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDTO {
    private Long id;
    private PlayerResponseDTO whitePlayer;
    private PlayerResponseDTO blackPlayer;
    private PlayerResponseDTO winner;
    private GameStatus status;
    private String timeControl;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
}
