package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.*;
import com.chess.game.util.Color;
import com.chess.game.domain.utils.PathValidator;

public class Queen extends Piece {
    public Queen(Color color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Move move) {
        int rowDiff = Math.abs(move.getToRow() - move.getFromRow());
        int colDiff = Math.abs(move.getToCol() - move.getFromCol());

        boolean isDiagonal = rowDiff == colDiff;
        boolean isStraight = move.getFromRow() == move.getToRow() || move.getFromCol() == move.getToCol();

        // Regla 1: solo recto o diagonal
        if (!(isDiagonal || isStraight)) return false;

        // Regla 2 y 3: camino libre y destino válido
        return PathValidator.isPathClear(board, move) && isDestinationValid(board, move);
    }

    private boolean isDestinationValid(Board board, Move move) {
        Piece target = board.getPiece(move.getTo());
        return target == null || target.getColor() != this.color;
    }
}
