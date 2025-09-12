package com.chess.game.presentation.controller;

import com.chess.game.persistence.entity.MoveEntity;
import com.chess.game.persistence.service.interfaces.IMoveService;
import com.chess.game.presentation.dto.move.CreateMoveDTO;
import com.chess.game.presentation.dto.move.MoveResponseDTO;
import com.chess.game.presentation.dto.move.UpdateMoveDTO;
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

    public MoveController(IMoveService moveService, MoveMapper moveMapper) {
        this.moveService = moveService;
        this.moveMapper = moveMapper;
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

    @PostMapping("/{id}")
    public ResponseEntity<MoveResponseDTO> create(@Valid @RequestBody CreateMoveDTO dto, @PathVariable Long id) {
        MoveEntity createdMove = moveService.create(dto, id);
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
