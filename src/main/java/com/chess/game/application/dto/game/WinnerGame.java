package com.chess.game.application.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WinnerGame {
    private Long winnerId;
    private String username;
    private String message;
}
