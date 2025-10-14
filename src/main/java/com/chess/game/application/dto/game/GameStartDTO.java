package com.chess.game.application.dto.game;

import com.chess.game.application.dto.player.PlayerProfileDTO;
import com.chess.game.util.enums.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameStartDTO {
    private Color color;
    private String code;
    private PlayerProfileDTO whitePlayer;
    private PlayerProfileDTO blackPlayer;
}