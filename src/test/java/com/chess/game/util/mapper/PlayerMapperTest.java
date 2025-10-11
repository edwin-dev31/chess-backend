package com.chess.game.util.mapper;

import com.chess.game.application.dto.player.PlayerResponseDTO;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.util.enums.PlayerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerMapperTest {

    private PlayerMapper playerMapper;

    @BeforeEach
    void setUp() {
        playerMapper = new PlayerMapper();
    }

    @Test
    void testMapTo_ShouldMapPlayerEntityToPlayerResponseDTO() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PlayerEntity playerEntity = PlayerEntity.builder()
                .id(1L)
                .username("Falcao")
                .email("falcao@example.com")
                .imageUrl("http://example.com/image.png")
                .rating(1500)
                .status(PlayerStatus.ONLINE)
                .createdAt(now)
                .lastOnline(now)
                .build();

        // When
        PlayerResponseDTO responseDTO = playerMapper.mapTo(playerEntity);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(playerEntity.getId());
        assertThat(responseDTO.getUsername()).isEqualTo(playerEntity.getUsername());
        assertThat(responseDTO.getEmail()).isEqualTo(playerEntity.getEmail());
        assertThat(responseDTO.getImageUrl()).isEqualTo(playerEntity.getImageUrl());
        assertThat(responseDTO.getRating()).isEqualTo(playerEntity.getRating());
        assertThat(responseDTO.getStatus()).isEqualTo(playerEntity.getStatus());
        assertThat(responseDTO.getCreatedAt()).isEqualTo(playerEntity.getCreatedAt());
        assertThat(responseDTO.getLastOnline()).isEqualTo(playerEntity.getLastOnline());
    }

    @Test
    void testMapToList_ShouldMapListOfEntitiesToListOfDTOs() {
        // Given
        PlayerEntity playerEntity = PlayerEntity.builder()
                .id(1L)
                .username("Falcao")
                .email("falcao@example.com")
                .build();
        List<PlayerEntity> playerEntities = Collections.singletonList(playerEntity);

        // When
        List<PlayerResponseDTO> responseDTOs = playerMapper.mapToList(playerEntities);

        // Then
        assertThat(responseDTOs).isNotNull();
        assertThat(responseDTOs).hasSize(1);
        assertThat(responseDTOs.get(0).getUsername()).isEqualTo("Falcao");
    }

    @Test
    void testMapFrom_ShouldThrowUnsupportedOperationException() {
        // Given
        PlayerResponseDTO dto = new PlayerResponseDTO();

        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> {
            playerMapper.mapFrom(dto);
        }, "Cannot map from PlayerResponseDTO to PlayerEntity");
    }

    @Test
    void testMapFromList_ShouldThrowUnsupportedOperationException() {
        // Given
        List<PlayerResponseDTO> dtoList = Collections.singletonList(new PlayerResponseDTO());

        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> {
            playerMapper.mapFromList(dtoList);
        }, "Cannot map from a list of PlayerResponseDTO to a list of PlayerEntity");
    }
}
