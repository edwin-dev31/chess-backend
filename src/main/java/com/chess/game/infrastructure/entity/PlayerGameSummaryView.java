package com.chess.game.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Immutable
@Table(name = "player_game_summary")
public class PlayerGameSummaryView {

    @Id
    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "player_id")
    private Long playerId;

    @Column(name = "opponent")
    private String opponent;

    @Column(name = "opponent_rating")
    private Integer opponentRating;

    @Column(name = "result")
    private String result;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time_control")
    private String timeControl;

    @Column(name = "moves")
    private Integer moves;
}
