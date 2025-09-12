package com.chess.game.application.dto.move;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMoveDTO {

    @NotBlank
    private String fromSquare;

    @NotBlank
    private String toSquare;

}
