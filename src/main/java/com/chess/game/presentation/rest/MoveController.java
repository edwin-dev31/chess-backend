package com.chess.game.presentation.rest;

import com.chess.game.application.dto.game.GameStatusDTO;
import com.chess.game.application.dto.game.MoveCreatedResponseDTO;
import com.chess.game.config.jwt.JwtUtil;
import com.chess.game.domain.MoveStatus;
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
    public ResponseEntity<MoveEntity> findById(@PathVariable Long id) {
        return moveService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        moveService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
