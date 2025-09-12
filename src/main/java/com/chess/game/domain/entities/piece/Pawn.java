package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.Board;
import com.chess.game.domain.entities.manager.Move;
import com.chess.game.domain.entities.manager.Position;
import com.chess.game.util.Color;

public class Pawn extends Piece {
    public Pawn(Color color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Move move) {
        int direction = (color == Color.WHITE) ? 1 : -1;

        return isSingleStep(board, move, direction)
                || isDoubleStep(board, move, direction)
                || isCapture(board, move, direction);
    }

    private boolean isSingleStep(Board board, Move move, int direction) {
        return move.getFromCol() == move.getToCol()
                && move.getToRow() == move.getFromRow() + direction
                && board.getPiece(move.getTo()) == null;
    }

    private boolean isDoubleStep(Board board, Move move, int direction) {
        if (move.getFromCol() != move.getToCol()) return false;

        boolean isStartingRow = (color == Color.WHITE && move.getFromRow() == 2)
                            ||  (color == Color.BLACK && move.getFromRow() == 7);

        if (!isStartingRow) return false;

        if (move.getToRow() != move.getFromRow() + 2 * direction) return false;

        Position middle = new Position(move.getFromRow() + direction, move.getFromCol());
        return board.getPiece(move.getTo()) == null
                && board.getPiece(middle) == null;
    }

    private boolean isCapture(Board board, Move move, int direction) {
        if (Math.abs(move.getToCol() - move.getFromCol()) != 1) return false;
        if (move.getToRow() != move.getFromRow() + direction) return false;

        Piece target = board.getPiece(move.getTo());
        return target != null && target.getColor() != this.color;
    }

}
