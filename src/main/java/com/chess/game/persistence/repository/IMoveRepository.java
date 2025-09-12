package com.chess.game.persistence.repository;

import com.chess.game.persistence.entity.MoveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IMoveRepository extends JpaRepository<MoveEntity, Long> {
    List<MoveEntity> findAllByGameId (Long id);
}
