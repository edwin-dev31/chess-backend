package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.*;
import com.chess.game.util.Color;
import com.chess.game.domain.utils.PathValidator;

public class Bishop extends Piece {
    public Bishop(Color color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Move move) {
        int rowDiff = Math.abs(move.getToRow() - move.getFromRow());
        int colDiff = Math.abs(move.getToCol() - move.getFromCol());

        // solo diagonal
        if (rowDiff != colDiff) return false;

        // camino libre + destino válido
        return PathValidator.isPathClear(board, move) && isDestinationValid(board, move);
    }

    private boolean isDestinationValid(Board board, Move move) {
        Piece target = board.getPiece(move.getTo());
        return target == null || target.getColor() != this.color;
    }
}
