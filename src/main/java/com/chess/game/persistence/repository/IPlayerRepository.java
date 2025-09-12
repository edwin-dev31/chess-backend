package com.chess.game.persistence.repository;

import com.chess.game.persistence.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPlayerRepository extends JpaRepository<PlayerEntity, Long> {
	Optional<PlayerEntity> findByEmail(String email);
    Optional<PlayerEntity> findByUsername(String username);
}
