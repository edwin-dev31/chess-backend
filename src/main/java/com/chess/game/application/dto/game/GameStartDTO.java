package com.chess.game.application.dto.game;

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
    public Color color;
    public String code;
    private Long opponentId;
    private String opponentUsername;
    private String opponentProfileImage;
}
