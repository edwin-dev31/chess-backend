package com.chess.game.util.mapper;

import com.chess.game.persistence.entity.MoveEntity;
import com.chess.game.presentation.dto.move.MoveResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MoveMapper implements MapperGeneric<MoveEntity, MoveResponseDTO> {

    private final PlayerMapper playerMapper;

    public MoveMapper(PlayerMapper playerMapper) {
        this.playerMapper = playerMapper;
    }

    @Override
    public MoveResponseDTO mapTo(MoveEntity moveEntity) {
        return MoveResponseDTO.builder()
                .id(moveEntity.getId())
                .gameId(moveEntity.getGame().getId())
                .player(playerMapper.mapTo(moveEntity.getPlayer()))
                .moveNumber(moveEntity.getMoveNumber())
                .fromSquare(moveEntity.getFromSquare())
                .toSquare(moveEntity.getToSquare())
                .piece(moveEntity.getPiece())
                .san(moveEntity.getSan())
                .fen(moveEntity.getFen())
                .createdAt(moveEntity.getCreatedAt())
                .build();
    }

    @Override
    public MoveEntity mapFrom(MoveResponseDTO moveResponseDTO) {
        throw new UnsupportedOperationException("Cannot map from MoveResponseDTO to MoveEntity");
    }

    @Override
    public List<MoveResponseDTO> mapToList(List<MoveEntity> moveEntities) {
        return moveEntities.stream().map(this::mapTo).collect(Collectors.toList());
    }

    @Override
    public List<MoveEntity> mapFromList(List<MoveResponseDTO> moveResponseDTOS) {
        throw new UnsupportedOperationException("Cannot map from a list of MoveResponseDTO to a list of MoveEntity");
    }
}
