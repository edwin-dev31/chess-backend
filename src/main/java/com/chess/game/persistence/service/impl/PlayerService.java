package com.chess.game.persistence.service.impl;

import com.chess.game.persistence.entity.PlayerEntity;
import com.chess.game.persistence.repository.IPlayerRepository;
import com.chess.game.persistence.service.interfaces.IPlayerService;
import com.chess.game.presentation.dto.player.CreatePlayerDTO;
import com.chess.game.presentation.dto.player.LoginPlayerDTO;
import com.chess.game.presentation.dto.player.UpdatePlayerDTO;
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
        PlayerEntity entity = PlayerEntity
                .builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .createdAt(LocalDateTime.now())
                .build();

        if (repository.findByEmail(dto.getEmail()).isPresent()){
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
        }

        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        PlayerEntity saved = repository.save(entity);
        return saved;
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
