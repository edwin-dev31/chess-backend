package com.chess.game.application.service.interfaces;

import com.chess.game.application.dto.game.InvitationDto;
import com.chess.game.infrastructure.entity.GameEntity;
import com.chess.game.application.dto.game.CreateGameDTO;

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
    String getCurrentPlayerColor(Long gameId);

    InvitationDto createInvitation(Long fromPlayerId, Long toUserId);
    InvitationDto acceptInvitation(Long fromPlayerId, Long toUserId);
    InvitationDto rejectInvitation(Long fromPlayerId, Long toUserId);
}
