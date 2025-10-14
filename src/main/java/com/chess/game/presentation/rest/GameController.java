package com.chess.game.presentation.rest;

import com.chess.game.application.dto.game.GameStartDTO;
import com.chess.game.application.dto.game.WinnerGame;
import com.chess.game.application.dto.player.PlayerProfileDTO;
import com.chess.game.config.jwt.JwtUtil;
import com.chess.game.domain.HashidsUtil;
import com.chess.game.infrastructure.entity.GameEntity;
import com.chess.game.application.service.interfaces.IGameService;
import com.chess.game.application.dto.game.CreateGameDTO;
import com.chess.game.application.dto.game.GameResponseDTO;
import com.chess.game.infrastructure.entity.PlayerEntity;
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
    public ResponseEntity<GameResponseDTO> startGame(@PathVariable String id, @RequestParam String time){

        GameEntity startedGame = gameService.startGame(HashidsUtil.decodeId(id), time);

        Long whitePlayerId = startedGame.getWhitePlayer().getId();
        Long blackPlayerId = startedGame.getBlackPlayer().getId();
        String code = HashidsUtil.encodeId(startedGame.getId());

        PlayerProfileDTO whitePlayer = getPlayerProfileDTO(whitePlayerId, startedGame.getWhitePlayer());
        PlayerProfileDTO blackPlayer = getPlayerProfileDTO(blackPlayerId, startedGame.getBlackPlayer());

        messagingTemplate.convertAndSendToUser(
                whitePlayerId.toString(),
                "/queue/start",
                GameStartDTO.builder()
                        .color(Color.WHITE)
                        .code(code)
                        .whitePlayer(whitePlayer)
                        .blackPlayer(blackPlayer)
                        .build()
        );
        messagingTemplate.convertAndSendToUser(
                blackPlayerId.toString(),
                "/queue/start",
                GameStartDTO.builder()
                        .color(Color.BLACK)
                        .code(code)
                        .whitePlayer(whitePlayer)
                        .blackPlayer(blackPlayer)
                        .build()
        );

        return ResponseEntity.ok(gameMapper.mapTo(startedGame));
    }

    private PlayerProfileDTO getPlayerProfileDTO(Long id, PlayerEntity player){
        return PlayerProfileDTO
                .builder()
                .id(id)
                .username(player.getUsername())
                .rating(player.getRating())
                .imageUrl(player.getImageUrl())
                .build();
    }
    @GetMapping("/{gameId}/color")
    public ResponseEntity<Map<String, String>> getCurrentPlayerColor(@PathVariable String gameId){
        String color = gameService.getCurrentPlayerColor(HashidsUtil.decodeId(gameId));
        return ResponseEntity.ok(Map.of("color", color));
    }

    @PostMapping("/{gameId}/leave")
    public ResponseEntity<Map<String, String>> leaveGame(@PathVariable String gameId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long playerId = jwt.extractId(token);

        WinnerGame msgWinner = gameService.leaveGame(HashidsUtil.decodeId(gameId), playerId);
        if(msgWinner != null){
            Map<String, String> error = Map.of("message", "Your opponent has left the game, "+ msgWinner.getUsername()+ " you are the winner");
            messagingTemplate.convertAndSendToUser(msgWinner.getWinnerId().toString(), "/queue/errors", error);
        }

        return ResponseEntity.ok(Map.of("leaved", msgWinner.getMessage()));
    }
}

