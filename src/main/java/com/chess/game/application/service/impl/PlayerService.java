package com.chess.game.application.service.impl;

import com.chess.game.application.dto.player.PlayerProfileDTO;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.infrastructure.repository.IPlayerRepository;
import com.chess.game.application.service.interfaces.IPlayerService;
import com.chess.game.application.dto.player.CreatePlayerDTO;
import com.chess.game.application.dto.player.UpdatePlayerDTO;
import com.chess.game.util.enums.PlayerStatus;
import com.chess.game.util.exception.DuplicateResourceException;
import com.chess.game.util.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService implements IPlayerService {
    private final IPlayerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PlayerService(IPlayerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PlayerEntity save(CreatePlayerDTO dto) {
        if (repository.findByEmail(dto.getEmail()).isPresent()){
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
        }

        if (repository.findByUsername(dto.getUsername()).isPresent()){
            throw new DuplicateResourceException("Username already exists");
        }

        PlayerEntity entity = PlayerEntity
                .builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .status(PlayerStatus.ONLINE)
                .createdAt(LocalDateTime.now())
                .build();

        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        PlayerEntity saved = repository.save(entity);
        return saved;
    }

    @Override
    public PlayerEntity updateStatus(Long id, PlayerStatus status){
        PlayerEntity playerToUpdate = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));
        playerToUpdate.setStatus(status);

        return repository.save(playerToUpdate);
    }

    @Override
    public Optional<PlayerEntity> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public List<PlayerEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<PlayerEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public PlayerProfileDTO getProfile(Long id) {
        PlayerEntity player = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Player not found"));
        return PlayerProfileDTO
                .builder()
                .id(player.getId())
                .username(player.getUsername())
                .rating(player.getRating())
                .imageUrl(player.getImageUrl())
                .status(player.getStatus())
                .build();
    }

    @Override
    public Optional<PlayerEntity> findByUsername(String name) {
        return repository.findByUsername(name);
    }


    @Override
    public PlayerStatus getPlayerStatus(Long playerId) {
        PlayerEntity player = repository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + playerId));

        return player.getStatus();
    }

    @Override
    public PlayerEntity update(Long id, UpdatePlayerDTO dto) {
        PlayerEntity playerToUpdate = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));

        if (dto.getUsername() != null) {
            Optional<PlayerEntity> temPlayer = repository.findByUsername(dto.getUsername());
            temPlayer.ifPresent(p -> {
                if (!p.getId().equals(id)) {
                    throw new DuplicateResourceException("Username already exists: " + dto.getUsername());
                }
            });
            playerToUpdate.setUsername(dto.getUsername());
        }

        if (dto.getPassword() != null) {
            playerToUpdate.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return repository.save(playerToUpdate);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Player not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
