package com.chess.game.util.mapper;

import com.chess.game.application.dto.game.GameResponseDTO;
import com.chess.game.application.dto.player.PlayerResponseDTO;
import com.chess.game.infrastructure.entity.GameEntity;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.util.enums.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameMapperTest {

    private GameMapper gameMapper;

    @BeforeEach
    void setUp() {
        PlayerMapper playerMapper = new PlayerMapper();
        gameMapper = new GameMapper(playerMapper);
    }

    private PlayerEntity createPlayer(Long id, String username) {
        return PlayerEntity.builder()
                .id(id)
                .username(username)
                .email(username.toLowerCase().replaceAll("\\s", ".") + "@example.com")
                .build();
    }

    @Test
    void testMapTo_ShouldMapGameEntityToGameResponseDTO_WithNonNullCurrentPlayer() {
        // Given
        PlayerEntity whitePlayer = createPlayer(1L, "Gabriel Garcia Marquez");
        PlayerEntity blackPlayer = createPlayer(2L, "Shakira");

        GameEntity gameEntity = GameEntity.builder()
                .id(100L)
                .whitePlayer(whitePlayer)
                .blackPlayer(blackPlayer)
                .currentPlayer(whitePlayer)
                .status(GameStatus.PLAYING)
                .timeControl("5+3")
                .createdAt(LocalDateTime.now())
                .finishedAt(null)
                .build();

        // When
        GameResponseDTO responseDTO = gameMapper.mapTo(gameEntity);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(gameEntity.getId());
        assertThat(responseDTO.getStatus()).isEqualTo(gameEntity.getStatus());
        assertThat(responseDTO.getTimeControl()).isEqualTo(gameEntity.getTimeControl());

        assertThat(responseDTO.getWhitePlayer()).isNotNull();
        assertThat(responseDTO.getWhitePlayer().getUsername()).isEqualTo("Gabriel Garcia Marquez");

        assertThat(responseDTO.getBlackPlayer()).isNotNull();
        assertThat(responseDTO.getBlackPlayer().getUsername()).isEqualTo("Shakira");

        assertThat(responseDTO.getCurrentPlayer()).isNotNull();
        assertThat(responseDTO.getCurrentPlayer().getUsername()).isEqualTo("Gabriel Garcia Marquez");
    }

    @Test
    void testMapTo_ShouldMapGameEntityToGameResponseDTO_WithNullCurrentPlayer() {
        // Given
        PlayerEntity whitePlayer = createPlayer(3L, "Fernando Botero");
        PlayerEntity blackPlayer = createPlayer(4L, "Sofia Vergara");

        GameEntity gameEntity = GameEntity.builder()
                .id(101L)
                .whitePlayer(whitePlayer)
                .blackPlayer(blackPlayer)
                .currentPlayer(null) // Null current player
                .status(GameStatus.FINISHED)
                .build();

        // When
        GameResponseDTO responseDTO = gameMapper.mapTo(gameEntity);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(gameEntity.getId());
        assertThat(responseDTO.getCurrentPlayer()).isNull();
    }

    @Test
    void testMapToList() {
        // Given
        PlayerEntity whitePlayer = createPlayer(5L, "Juanes");
        PlayerEntity blackPlayer = createPlayer(6L, "Carlos Vives");
        GameEntity gameEntity = GameEntity.builder().id(102L).whitePlayer(whitePlayer).blackPlayer(blackPlayer).status(GameStatus.WAITING).build();
        List<GameEntity> gameEntities = Collections.singletonList(gameEntity);

        // When
        List<GameResponseDTO> responseDTOs = gameMapper.mapToList(gameEntities);

        // Then
        assertThat(responseDTOs).isNotNull().hasSize(1);
        assertThat(responseDTOs.get(0).getWhitePlayer().getUsername()).isEqualTo("Juanes");
    }

    @Test
    void testMapFrom_ShouldThrowUnsupportedOperationException() {
        // Given
        GameResponseDTO dto = new GameResponseDTO();

        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> {
            gameMapper.mapFrom(dto);
        });
    }

    @Test
    void testMapFromList_ShouldThrowUnsupportedOperationException() {
        // Given
        List<GameResponseDTO> dtoList = Collections.singletonList(new GameResponseDTO());

        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> {
            gameMapper.mapFromList(dtoList);
        });
    }
}
