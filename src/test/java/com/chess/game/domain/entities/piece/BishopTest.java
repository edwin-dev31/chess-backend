package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.Board;
import com.chess.game.domain.entities.manager.Move;
import com.chess.game.domain.entities.manager.Position;
import com.chess.game.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

    private Board board;
    private Bishop bishop;

    @BeforeEach
    void setUp() {
        board = new Board();
        bishop = new Bishop(Color.WHITE, new Position(4, 4));
        board.setPiece(bishop.getPosition(), bishop);
    }

    static Stream<int[]> validMoveProvider() {
        return Stream.of(
            new int[]{2, 2}, new int[]{2, -2}, new int[]{-2, 2}, new int[]{-2, -2}
        );
    }

    @ParameterizedTest
    @MethodSource("validMoveProvider")
    void testValidMoves(int[] moveOffsets) {
        int dRow = moveOffsets[0];
        int dCol = moveOffsets[1];
        Position from = bishop.getPosition();
        Position to = new Position(from.getRow() + dRow, from.getCol() + dCol);
        Move move = new Move(from, to, null);
        assertTrue(bishop.canMove(board, move));
    }

    @Test
    void testInvalidMove_NotDiagonal() {
        Move move = new Move(bishop.getPosition(), new Position(5, 4), null);
        assertFalse(bishop.canMove(board, move));
    }

    @Test
    void testInvalidMove_PieceInPath() {
        Pawn blockingPawn = new Pawn(Color.WHITE, new Position(5, 5));
        board.setPiece(blockingPawn.getPosition(), blockingPawn);
        Move move = new Move(bishop.getPosition(), new Position(6, 6), null);
        assertFalse(bishop.canMove(board, move));
    }

    @Test
    void testValidCapture() {
        Pawn opponentPawn = new Pawn(Color.BLACK, new Position(6, 6));
        board.setPiece(opponentPawn.getPosition(), opponentPawn);
        Move move = new Move(bishop.getPosition(), opponentPawn.getPosition(), null);
        assertTrue(bishop.canMove(board, move));
    }

    @Test
    void testInvalidCapture_SameColor() {
        Pawn sameColorPawn = new Pawn(Color.WHITE, new Position(6, 6));
        board.setPiece(sameColorPawn.getPosition(), sameColorPawn);
        Move move = new Move(bishop.getPosition(), sameColorPawn.getPosition(), null);
        assertFalse(bishop.canMove(board, move));
    }
}
