package com.chess.game.infrastructure.entity;

import com.chess.game.util.enums.GameStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "games")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "white_player_id", nullable = false)
    private PlayerEntity whitePlayer;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "black_player_id", nullable = true)
    private PlayerEntity blackPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_player_id")
    private PlayerEntity currentPlayer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameStatus status = GameStatus.WAITING;

    @Column(length = 50)
    private String timeControl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime finishedAt;

    @Column(columnDefinition = "TEXT")
    private String fen;

    @Column(columnDefinition = "TEXT")
    private String pgn;
}
