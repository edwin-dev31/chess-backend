package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.Board;
import com.chess.game.domain.entities.manager.Move;
import com.chess.game.domain.entities.manager.Position;
import com.chess.game.util.Color;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        Knight knight = new Knight(Color.WHITE, new Position(1, 2)); // caballo en b1
        board.setPiece(knight.getPosition(), knight);
        Pawn pawn = new Pawn(Color.WHITE,  new Position(5, 4));
        board.setPiece(pawn.getPosition(), pawn);
// Movimiento válido: b1 → c3
        Move move1 = new Move(new Position(6, 6), new Position(5, 4), null);
        System.out.println(knight.canMove(board, move1)); // true

// Movimiento inválido: b1 → b3 (no es L)
        Move move2 = new Move(new Position(1, 2), new Position(3, 2), null);
       // System.out.println(knight.canMove(board, move2)); // false

    }

}
