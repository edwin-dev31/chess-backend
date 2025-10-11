package com.chess.game.util.mapper;

import com.chess.game.application.dto.move.MoveResponseDTO;
import com.chess.game.infrastructure.entity.GameEntity;
import com.chess.game.infrastructure.entity.MoveEntity;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.util.enums.PieceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveMapperTest {

    private MoveMapper moveMapper;

    @BeforeEach
    void setUp() {
        // The constructor requires a PlayerMapper, so we provide a dummy one.
        moveMapper = new MoveMapper(new PlayerMapper());
    }

    @Test
    void testMapTo_ShouldMapMoveEntityToMoveResponseDTO() {
        // Given
        PlayerEntity player = PlayerEntity.builder().id(1L).username("Nairo Quintana").build();
        GameEntity game = GameEntity.builder().id(100L).build();

        MoveEntity moveEntity = MoveEntity.builder()
                .id(1L)
                .game(game)
                .player(player)
                .moveNumber(1)
                .fromSquare("e2")
                .toSquare("e4")
                .piece(PieceType.PAWN)
                .san("e4")
                .fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1")
                .build();

        // When
        MoveResponseDTO responseDTO = moveMapper.mapEntityTo(moveEntity);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(moveEntity.getId());
        assertThat(responseDTO.getGameId()).isEqualTo(game.getId());
        assertThat(responseDTO.getPlayer()).isEqualTo(player.getId());
        assertThat(responseDTO.getMoveNumber()).isEqualTo(moveEntity.getMoveNumber());
        assertThat(responseDTO.getFromSquare()).isEqualTo(moveEntity.getFromSquare());
        assertThat(responseDTO.getToSquare()).isEqualTo(moveEntity.getToSquare());
        assertThat(responseDTO.getPiece()).isEqualTo(moveEntity.getPiece());
        assertThat(responseDTO.getSan()).isEqualTo(moveEntity.getSan());
        assertThat(responseDTO.getFen()).isEqualTo(moveEntity.getFen());
    }

    @Test
    void testMapToList() {
        // Given
        PlayerEntity player = PlayerEntity.builder().id(2L).username("Egan Bernal").build();
        GameEntity game = GameEntity.builder().id(101L).build();
        MoveEntity moveEntity = MoveEntity.builder().id(2L).game(game).player(player).fromSquare("g1").toSquare("f3").san("Nf3").build();
        List<MoveEntity> moveEntities = Collections.singletonList(moveEntity);

        // When
        List<MoveResponseDTO> responseDTOs = moveMapper.mapToList(moveEntities);

        // Then
        assertThat(responseDTOs).isNotNull().hasSize(1);
        assertThat(responseDTOs.get(0).getSan()).isEqualTo("Nf3");
        assertThat(responseDTOs.get(0).getPlayer()).isEqualTo(2L);
    }

    @Test
    void testMapFrom_ShouldThrowUnsupportedOperationException() {
        // Given
        MoveResponseDTO dto = new MoveResponseDTO();

        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> {
            moveMapper.mapFrom(dto);
        });
    }

    @Test
    void testMapFromList_ShouldThrowUnsupportedOperationException() {
        // Given
        List<MoveResponseDTO> dtoList = Collections.singletonList(new MoveResponseDTO());

        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> {
            moveMapper.mapFromList(dtoList);
        });
    }
}
