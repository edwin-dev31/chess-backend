package com.chess.game.presentation.resController;

import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.application.service.interfaces.IPlayerService;
import com.chess.game.application.dto.player.PlayerResponseDTO;
import com.chess.game.application.dto.player.UpdatePlayerDTO;
import com.chess.game.util.mapper.PlayerMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final IPlayerService playerService;
    private final PlayerMapper playerMapper;

    public PlayerController(IPlayerService playerService, PlayerMapper playerMapper) {
        this.playerService = playerService;
        this.playerMapper = playerMapper;
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
}

