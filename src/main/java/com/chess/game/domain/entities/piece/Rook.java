package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.*;
import com.chess.game.util.Color;
import com.chess.game.domain.utils.PathValidator;

public class Rook extends Piece {
    public Rook(Color color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Move move) {
        boolean sameRow = move.getFromRow() == move.getToRow();
        boolean sameCol = move.getFromCol() == move.getToCol();

        // solo fila o columna
        if (!(sameRow || sameCol)) return false;

        // camino libre + destino válido
        return PathValidator.isPathClear(board, move) && isDestinationValid(board, move);
    }

    private boolean isDestinationValid(Board board, Move move) {
        Piece target = board.getPiece(move.getTo());
        return target == null || target.getColor() != this.color;
    }
}
