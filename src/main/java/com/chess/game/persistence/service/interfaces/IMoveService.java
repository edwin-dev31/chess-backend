package com.chess.game.persistence.service.interfaces;

import com.chess.game.persistence.entity.MoveEntity;
import com.chess.game.persistence.entity.PlayerEntity;
import com.chess.game.presentation.dto.move.CreateMoveDTO;
import com.chess.game.presentation.dto.move.UpdateMoveDTO;

import java.util.List;
import java.util.Optional;

public interface IMoveService {
    MoveEntity create(CreateMoveDTO dto, Long playerId);
    List<MoveEntity> findAllByGameId(Long gameId);
    Optional<MoveEntity> findById(Long id);
    MoveEntity update(Long id, UpdateMoveDTO dto);
    void deleteById(Long id);
}
