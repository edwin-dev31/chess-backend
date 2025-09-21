package com.chess.game.application.service.interfaces;

import com.chess.game.application.dto.player.PlayerProfileDTO;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.application.dto.player.CreatePlayerDTO;
import com.chess.game.application.dto.player.UpdatePlayerDTO;

import java.util.List;
import java.util.Optional;

public interface IPlayerService {
    List<PlayerEntity> findAll();
    Optional<PlayerEntity> findById(Long id);
    PlayerProfileDTO getProfile(Long id);
    Optional<PlayerEntity> findByUsername(String name);
    Optional<PlayerEntity> findByEmail(String email);
    PlayerEntity save(CreatePlayerDTO dto);
    PlayerEntity update(Long id, UpdatePlayerDTO dto);
    void deleteById(Long id);
}
