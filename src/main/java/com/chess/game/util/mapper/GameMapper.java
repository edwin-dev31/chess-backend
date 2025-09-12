package com.chess.game.util.mapper;

import com.chess.game.infrastructure.entity.GameEntity;
import com.chess.game.application.dto.game.GameResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameMapper implements MapperGeneric<GameEntity, GameResponseDTO> {

    private final PlayerMapper playerMapper;

    public GameMapper(PlayerMapper playerMapper) {
        this.playerMapper = playerMapper;
    }

    @Override
    public GameResponseDTO mapTo(GameEntity gameEntity) {
        return GameResponseDTO.builder()
                .id(gameEntity.getId())
                .whitePlayer(playerMapper.mapTo(gameEntity.getWhitePlayer()))
                .blackPlayer(playerMapper.mapTo(gameEntity.getBlackPlayer()))
                .currentPlayer(gameEntity.getCurrentPlayer() != null ? playerMapper.mapTo(gameEntity.getCurrentPlayer()) : null)
                .status(gameEntity.getStatus())
                .timeControl(gameEntity.getTimeControl())
                .createdAt(gameEntity.getCreatedAt())
                .finishedAt(gameEntity.getFinishedAt())
                .build();
    }

    @Override
    public GameEntity mapFrom(GameResponseDTO gameResponseDTO) {
        throw new UnsupportedOperationException("Cannot map from GameResponseDTO to GameEntity");
    }

    @Override
    public List<GameResponseDTO> mapToList(List<GameEntity> gameEntities) {
        return gameEntities.stream().map(this::mapTo).collect(Collectors.toList());
    }

    @Override
    public List<GameEntity> mapFromList(List<GameResponseDTO> gameResponseDTOS) {
        throw new UnsupportedOperationException("Cannot map from a list of GameResponseDTO to a list of GameEntity");
    }
}
