package com.chess.game.application.dto.game;

import com.chess.game.util.enums.Color;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GameStartDTO {
    public Color color;
    public String code;
}
