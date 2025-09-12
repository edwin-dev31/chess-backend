package com.chess.game.presentation.dto.move;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMoveDTO {
    private String fromSquare;
    private String toSquare;
}
