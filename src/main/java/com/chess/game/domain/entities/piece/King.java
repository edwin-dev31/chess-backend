package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.*;
import com.chess.game.util.Color;

public class King extends Piece {
    public King(Color color, Position position) {
        super(color, position);
    }

    @Override
    public boolean canMove(Board board, Move move) {
        int rowDiff = Math.abs(move.getToRow() - move.getFromRow());
        int colDiff = Math.abs(move.getToCol() - move.getFromCol());

        // Movimiento normal: una casilla en cualquier dirección
        if (rowDiff <= 1 && colDiff <= 1) {
            return isDestinationValid(board, move);
        }

        // Enroque: mover 2 casillas en horizontal
        if (rowDiff == 0 && colDiff == 2) {
            return canCastle(board, move);
        }

        return false;
    }

    private boolean canCastle(Board board, Move move) {
        int row = move.getFromRow();

        // Blancas: fila 1, Negras: fila 8
        if ((color == Color.WHITE && row != 1) || (color == Color.BLACK && row != 8)) {
            return false;
        }

        // Enroque corto (lado rey)
        if (move.getToCol() == 7) {
            return canCastleWithRook(board, new Position(row, 8));
        }

        // Enroque largo (lado dama)
        if (move.getToCol() == 3) {
            return canCastleWithRook(board, new Position(row, 1));
        }

        return false;
    }

    private boolean canCastleWithRook(Board board, Position rookPos) {
        Piece rook = board.getPiece(rookPos);

        // Verificar que sea una torre del mismo color
        if (!(rook instanceof Rook) || rook.getColor() != this.color) {
            return false;
        }

        // Camino libre entre rey y torre
        int step = (rookPos.getCol() < position.getCol()) ? -1 : 1;
        for (int col = position.getCol() + step; col != rookPos.getCol(); col += step) {
            if (board.getPiece(new Position(position.getRow(), col)) != null) {
                return false;
            }
        }

        // TODO: verificar que el rey no esté en jaque, ni pase por casillas atacadas
        // if (board.isSquareAttacked(...)) return false;

        return true;
    }

    private boolean isDestinationValid(Board board, Move move) {
        Piece target = board.getPiece(move.getTo());
        return target == null || target.getColor() != this.color;
    }
}
