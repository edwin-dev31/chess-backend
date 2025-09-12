package com.chess.game.util.mapper;

import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.application.dto.player.PlayerResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlayerMapper implements MapperGeneric<PlayerEntity, PlayerResponseDTO> {

    @Override
    public PlayerResponseDTO mapTo(PlayerEntity playerEntity) {
        return PlayerResponseDTO.builder()
                .id(playerEntity.getId())
                .username(playerEntity.getUsername())
                .email(playerEntity.getEmail())
                .imageUrl(playerEntity.getImageUrl())
                .rating(playerEntity.getRating())
                .status(playerEntity.getStatus())
                .createdAt(playerEntity.getCreatedAt())
                .lastOnline(playerEntity.getLastOnline())
                .build();
    }

    @Override
    public PlayerEntity mapFrom(PlayerResponseDTO playerResponseDTO) {
        throw new UnsupportedOperationException("Cannot map from PlayerResponseDTO to PlayerEntity");
    }

    @Override
    public List<PlayerResponseDTO> mapToList(List<PlayerEntity> playerEntities) {
        return playerEntities.stream().map(this::mapTo).collect(Collectors.toList());
    }

    @Override
    public List<PlayerEntity> mapFromList(List<PlayerResponseDTO> playerResponseDTOS) {
        throw new UnsupportedOperationException("Cannot map from a list of PlayerResponseDTO to a list of PlayerEntity");
    }
}
