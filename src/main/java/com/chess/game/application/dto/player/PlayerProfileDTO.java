package com.chess.game.application.dto.player;

import com.chess.game.util.enums.PlayerStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerProfileDTO {
    private Long id;
    private String username;
    private Integer rating;
    private String imageUrl;
    private PlayerStatus status;

}
