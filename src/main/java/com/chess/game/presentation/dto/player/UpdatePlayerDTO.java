package com.chess.game.presentation.dto.player;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlayerDTO {
    @Size(min = 3, max = 50)
    private String username;

    @Size(min = 8)
    private String password;
}
