package com.chess.game.presentation.rest;

import com.chess.game.config.jwt.JwtUtil;
import com.chess.game.domain.HashidsUtil;
import com.chess.game.infrastructure.entity.GameEntity;
import com.chess.game.application.service.interfaces.IGameService;
import com.chess.game.application.dto.game.CreateGameDTO;
import com.chess.game.application.dto.game.GameResponseDTO;
import com.chess.game.util.enums.Color;
import com.chess.game.util.mapper.GameMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final IGameService gameService;
    private final GameMapper gameMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwt;

    public GameController(IGameService gameService, GameMapper gameMapper, SimpMessagingTemplate messagingTemplate, JwtUtil jwt) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
        this.messagingTemplate = messagingTemplate;
        this.jwt = jwt;
    }

    @GetMapping
    public ResponseEntity<List<GameResponseDTO>> findAll() {
        List<GameEntity> games = gameService.findAll();
        return ResponseEntity.ok(gameMapper.mapToList(games));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDTO> findById(@PathVariable String id) {
        return gameService.findById(HashidsUtil.decodeId(id))
                .map(game -> ResponseEntity.ok(gameMapper.mapTo(game)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody CreateGameDTO dto){
        return ResponseEntity.ok(gameService.createGame(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        gameService.deleteById(HashidsUtil.decodeId(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pgn")
    public ResponseEntity<Map<String, String>> getPgn(@PathVariable String id) {
        String pgn = gameService.getPgnByGameId(HashidsUtil.decodeId(id));
        return ResponseEntity.ok(Map.of("pgn", pgn));
    }


    @PostMapping("/{id}/start")
    public ResponseEntity<GameResponseDTO> startGame(@PathVariable String id){

        GameEntity startedGame = gameService.startGame(HashidsUtil.decodeId(id));

        String whitePlayerId = startedGame.getWhitePlayer().getId().toString();
        String blackPlayerId = startedGame.getBlackPlayer().getId().toString();

        messagingTemplate.convertAndSendToUser(
                whitePlayerId,
                "/queue/start",
                Color.WHITE
        );
        messagingTemplate.convertAndSendToUser(
                blackPlayerId,
                "/queue/start",
                Color.BLACK
        );

        return ResponseEntity.ok(gameMapper.mapTo(startedGame));
    }

    @GetMapping("/{gameId}/color")
    public ResponseEntity<Map<String, String>> getCurrentPlayerColor(@PathVariable String gameId){
        String color = gameService.getCurrentPlayerColor(HashidsUtil.decodeId(gameId));
        return ResponseEntity.ok(Map.of("color", color));
    }
}

