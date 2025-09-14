package com.chess.game.infrastructure.repository;

import com.chess.game.infrastructure.entity.MoveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMoveRepository extends JpaRepository<MoveEntity, Long> {
    List<MoveEntity> findAllByGameId (Long id);
    List<MoveEntity> findAllByGameIdOrderByMoveNumberAsc(Long id);
}
