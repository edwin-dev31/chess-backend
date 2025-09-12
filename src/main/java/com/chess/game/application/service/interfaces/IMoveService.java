package com.chess.game.application.service.interfaces;

import com.chess.game.infrastructure.entity.MoveEntity;
import com.chess.game.application.dto.move.CreateMoveDTO;
import com.chess.game.application.dto.move.UpdateMoveDTO;

import java.util.List;
import java.util.Optional;

public interface IMoveService {
    MoveEntity create(CreateMoveDTO dto, Long playerId, Long gameId);
    List<MoveEntity> findAllByGameId(Long gameId);
    Optional<MoveEntity> findById(Long id);
    MoveEntity update(Long id, UpdateMoveDTO dto);
    void deleteById(Long id);
}
