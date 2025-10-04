package com.chess.game.presentation.rest;

import com.chess.game.application.dto.player.PlayerOnlineDTO;
import com.chess.game.application.dto.player.PlayerProfileDTO;
import com.chess.game.config.jwt.JwtUtil;
import com.chess.game.config.socket.PresenceEventListener;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.application.service.interfaces.IPlayerService;
import com.chess.game.application.dto.player.PlayerResponseDTO;
import com.chess.game.application.dto.player.UpdatePlayerDTO;
import com.chess.game.util.mapper.PlayerMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final IPlayerService playerService;
    private final PlayerMapper playerMapper;
    private final PresenceEventListener presence;
    private final JwtUtil jwt;

    public PlayerController(IPlayerService playerService, PlayerMapper playerMapper, PresenceEventListener presence, JwtUtil jwt) {
        this.playerService = playerService;
        this.playerMapper = playerMapper;
        this.presence = presence;
        this.jwt = jwt;
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponseDTO>> findAll() {
        List<PlayerEntity> players = playerService.findAll();
        return ResponseEntity.ok(playerMapper.mapToList(players));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponseDTO> findById(@PathVariable Long id) {
        return playerService.findById(id)
                .map(player -> ResponseEntity.ok(playerMapper.mapTo(player)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UpdatePlayerDTO dto) {
        PlayerEntity updatedPlayer = playerService.update(id, dto);
        return ResponseEntity.ok(playerMapper.mapTo(updatedPlayer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        playerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<PlayerProfileDTO> getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long playerId = jwt.extractId(token);

        return ResponseEntity.ok(playerService.getProfile(playerId));
    }

    @GetMapping("/online")
    public Set<PlayerOnlineDTO> getOnlinePlayers() {
        return presence.getOnlinePlayers();
    }

    @SubscribeMapping("/online-players")
    public Set<PlayerOnlineDTO> sendCurrentPlayers() {
        return presence.getOnlinePlayers();
    }
}

