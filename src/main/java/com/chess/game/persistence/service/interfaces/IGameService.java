package com.chess.game.persistence.service.interfaces;

import com.chess.game.persistence.entity.GameEntity;
import com.chess.game.presentation.dto.game.CreateGameDTO;

import com.chess.game.presentation.dto.move.CreateMoveDTO;

import java.util.List;
import java.util.Optional;

public interface IGameService {
    GameEntity createGame(CreateGameDTO dto);
    String getFenByGameId(Long gameId);
    String getPgnByGameId(Long gameId);
    List<GameEntity> findAll();
    Optional<GameEntity> findById(Long id);
    void deleteById(Long id);
    GameEntity startGame(Long gameId);
}
