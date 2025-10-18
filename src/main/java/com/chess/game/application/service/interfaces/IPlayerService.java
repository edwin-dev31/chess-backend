package com.chess.game.application.service.interfaces;

import com.chess.game.application.dto.player.PlayerProfileDTO;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.application.dto.player.CreatePlayerDTO;
import com.chess.game.application.dto.player.UpdatePlayerDTO;
import com.chess.game.infrastructure.entity.PlayerGameSummaryView;
import com.chess.game.util.enums.PlayerStatus;

import java.util.List;
import java.util.Optional;

public interface IPlayerService {
    List<PlayerEntity> findAll();
    Optional<PlayerEntity> findById(Long id);
    PlayerProfileDTO getProfile(Long id);
    Optional<PlayerEntity> findByUsername(String name);
    Optional<PlayerEntity> findByEmail(String email);
    PlayerStatus getPlayerStatus(Long playerId);
    PlayerEntity save(CreatePlayerDTO dto);
    PlayerEntity updateStatus(Long id, PlayerStatus status);
    PlayerEntity update(Long id, UpdatePlayerDTO dto);
    List<PlayerGameSummaryView> playerSummary(Long id);
    void deleteById(Long id);
}
