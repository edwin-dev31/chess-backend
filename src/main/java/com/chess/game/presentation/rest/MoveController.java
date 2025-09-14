package com.chess.game.presentation.rest;

import com.chess.game.config.jwt.JwtUtil;
import com.chess.game.infrastructure.entity.MoveEntity;
import com.chess.game.application.service.interfaces.IMoveService;
import com.chess.game.application.dto.move.CreateMoveDTO;
import com.chess.game.application.dto.move.MoveResponseDTO;
import com.chess.game.application.dto.move.UpdateMoveDTO;
import com.chess.game.util.mapper.MoveMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moves")
public class MoveController {

    private final IMoveService moveService;
    private final MoveMapper moveMapper;
    private final JwtUtil jwt;

    public MoveController(IMoveService moveService, MoveMapper moveMapper, JwtUtil jwt) {
        this.moveService = moveService;
        this.moveMapper = moveMapper;
        this.jwt = jwt;
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<MoveResponseDTO>> findAllByGameId(@PathVariable Long gameId) {
        List<MoveEntity> moves = moveService.findAllByGameId(gameId);
        return ResponseEntity.ok(moveMapper.mapToList(moves));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoveResponseDTO> findById(@PathVariable Long id) {
        return moveService.findById(id)
                .map(move -> ResponseEntity.ok(moveMapper.mapTo(move)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<MoveResponseDTO> create(@PathVariable Long gameId,
                                                  @Valid @RequestBody CreateMoveDTO dto,
                                                  @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long playerId = jwt.extractId(token);

        MoveEntity createdMove = moveService.create(dto, playerId, gameId);
        return ResponseEntity.status(HttpStatus.CREATED).body(moveMapper.mapTo(createdMove));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MoveResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateMoveDTO dto) {
        MoveEntity updatedMove = moveService.update(id, dto);
        return ResponseEntity.ok(moveMapper.mapTo(updatedMove));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        moveService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
