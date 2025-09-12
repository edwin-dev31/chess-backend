package com.chess.game.presentation.dto.player;

import com.chess.game.util.PlayerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String imageUrl;
    private Integer rating;
    private PlayerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastOnline;
}
