package com.chess.game.domain.entities.manager;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Position {
    private int row;
    private int col;

    @Override
    public String toString() {
        char file = (char) ('a' + (col - 1));
        int rank = row;
        return "" + file + rank;
    }

    public static Position fromAlgebraic(String algebraic) {
        // Ej: "e4" => row=4, col=5
        int col = (algebraic.charAt(0) - 'a') + 1;
        int row = Character.getNumericValue(algebraic.charAt(1));
        return new Position(row, col);
    }
}
