package com.chess.game.application.dto.player;

import com.chess.game.util.enums.PlayerStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class PlayerOnlineDTO {
    private Long id;
    private String username;
    private String imageUrl;
    private PlayerStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerOnlineDTO)) return false;
        PlayerOnlineDTO that = (PlayerOnlineDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
