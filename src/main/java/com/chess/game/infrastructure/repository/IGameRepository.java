package com.chess.game.infrastructure.repository;

import com.chess.game.infrastructure.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGameRepository extends JpaRepository<GameEntity, Long> {
}
