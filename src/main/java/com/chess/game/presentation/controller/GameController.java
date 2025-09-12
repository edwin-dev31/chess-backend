package com.chess.game.presentation.controller;

import com.chess.game.persistence.entity.GameEntity;
import com.chess.game.persistence.service.interfaces.IGameService;
import com.chess.game.presentation.dto.game.CreateGameDTO;
import com.chess.game.presentation.dto.game.GameResponseDTO;
import com.chess.game.util.mapper.GameMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final IGameService gameService;
    private final GameMapper gameMapper;

    public GameController(IGameService gameService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
    }

    @GetMapping
    public ResponseEntity<List<GameResponseDTO>> findAll() {
        List<GameEntity> games = gameService.findAll();
        return ResponseEntity.ok(gameMapper.mapToList(games));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDTO> findById(@PathVariable Long id) {
        return gameService.findById(id)
                .map(game -> ResponseEntity.ok(gameMapper.mapTo(game)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        gameService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/fen")
    public ResponseEntity<String> getFen(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getFenByGameId(id));
    }

    @GetMapping("/{id}/pgn")
    public ResponseEntity<String> getPgn(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getPgnByGameId(id));
    }
}

