package com.chess.game.domain;

public enum GameEndStatus {
    IN_PROGRESS,
    MATED, // Checkmate
    DRAW // Includes stalemate, fifty-move rule, etc.
}
