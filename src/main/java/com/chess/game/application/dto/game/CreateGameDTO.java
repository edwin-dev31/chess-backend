package com.chess.game.application.dto.game;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameDTO {
    @NotNull
    private Long whitePlayerId;
    @NotNull
    private Long blackPlayerId;
    @NotNull
    private String timeControl;
}
