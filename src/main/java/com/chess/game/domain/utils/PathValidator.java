package com.chess.game.domain.utils;

import com.chess.game.domain.entities.manager.Board;
import com.chess.game.domain.entities.manager.Move;
import com.chess.game.domain.entities.manager.Position;

public class PathValidator {

    public static boolean isPathClear(Board board, Move move) {
        int rowStep = Integer.compare(move.getToRow(), move.getFromRow());
        int colStep = Integer.compare(move.getToCol(), move.getFromCol());

        int currentRow = move.getFromRow() + rowStep;
        int currentCol = move.getFromCol() + colStep;

        // avanzar hasta justo antes de la casilla destino
        while (currentRow != move.getToRow() || currentCol != move.getToCol()) {
            if (board.getPiece(new Position(currentRow, currentCol)) != null) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return true;
    }
}
