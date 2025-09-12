package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.*;
import com.chess.game.util.Color;

public class Knight extends Piece {
    public Knight(Color color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Move move) {
        int rowDiff = Math.abs(move.getToRow() - move.getFromRow());
        int colDiff = Math.abs(move.getToCol() - move.getFromCol());

        // Regla 1: debe ser movimiento en L
        boolean isLShape = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
        if (!isLShape) return false;

        // Regla 2 y 3: destino válido
        return isDestinationValid(board, move);
    }

    private boolean isDestinationValid(Board board, Move move) {
        Piece target = board.getPiece(move.getTo());
        return target == null || target.getColor() != this.color;
    }
}
