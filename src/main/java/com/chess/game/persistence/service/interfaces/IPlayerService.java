package com.chess.game.persistence.service.interfaces;

import com.chess.game.persistence.entity.PlayerEntity;
import com.chess.game.presentation.dto.player.CreatePlayerDTO;
import com.chess.game.presentation.dto.player.UpdatePlayerDTO;

import java.util.List;
import java.util.Optional;

public interface IPlayerService {
    List<PlayerEntity> findAll();
    Optional<PlayerEntity> findById(Long id);
    Optional<PlayerEntity> findByEmail(String email);
    PlayerEntity save(CreatePlayerDTO dto);
    PlayerEntity update(Long id, UpdatePlayerDTO dto);
    void deleteById(Long id);
}
