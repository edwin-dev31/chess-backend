package com.chess.game.persistence.repository;

import com.chess.game.persistence.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGameRepository extends JpaRepository<GameEntity, Long> {
}
