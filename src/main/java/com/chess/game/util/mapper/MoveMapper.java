package com.chess.game.util.mapper;

import com.chess.game.application.dto.game.MoveCreatedResponseDTO;
import com.chess.game.infrastructure.entity.MoveEntity;
import com.chess.game.application.dto.move.MoveResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MoveMapper {

    private final PlayerMapper playerMapper;

    public MoveMapper(PlayerMapper playerMapper) {
        this.playerMapper = playerMapper;
    }


    public MoveResponseDTO mapTo(MoveCreatedResponseDTO moveEntity) {
        return MoveResponseDTO.builder()
                .moveNumber(moveEntity.getMoveNumber())
                .fromSquare(moveEntity.getFromSquare())
                .toSquare(moveEntity.getToSquare())
                .san(moveEntity.getSan())
                .fen(moveEntity.getFen())
                .build();
    }

    public MoveResponseDTO mapEntityTo(MoveEntity moveEntity) {
        return MoveResponseDTO.builder()
                .id(moveEntity.getId())
                .gameId(moveEntity.getGame().getId())
                .player(moveEntity.getPlayer().getId())
                .moveNumber(moveEntity.getMoveNumber())
                .fromSquare(moveEntity.getFromSquare())
                .toSquare(moveEntity.getToSquare())
                .piece(moveEntity.getPiece())
                .san(moveEntity.getSan())
                .fen(moveEntity.getFen())
                .build();
    }
    public MoveEntity mapFrom(MoveResponseDTO moveResponseDTO) {
        throw new UnsupportedOperationException("Cannot map from MoveResponseDTO to MoveEntity");
    }

    public List<MoveResponseDTO> mapToList(List<MoveEntity> moveEntities) {
        return moveEntities.stream().map(this::mapEntityTo).collect(Collectors.toList());
    }

    public List<MoveEntity> mapFromList(List<MoveResponseDTO> moveResponseDTOS) {
        throw new UnsupportedOperationException("Cannot map from a list of MoveResponseDTO to a list of MoveEntity");
    }
}
