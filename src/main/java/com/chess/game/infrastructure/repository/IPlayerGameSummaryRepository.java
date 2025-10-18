package com.chess.game.infrastructure.repository;

import com.chess.game.infrastructure.entity.PlayerGameSummaryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPlayerGameSummaryRepository extends JpaRepository<PlayerGameSummaryView, Long> {
    List<PlayerGameSummaryView> findByPlayerId(Long playerId);
}
