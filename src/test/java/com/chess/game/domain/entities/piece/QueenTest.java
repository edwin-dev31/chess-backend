package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.Board;
import com.chess.game.domain.entities.manager.Move;
import com.chess.game.domain.entities.manager.Position;
import com.chess.game.util.enums.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class QueenTest {

    private Board board;
    private Queen queen;

    @BeforeEach
    void setUp() {
        board = new Board();
        queen = new Queen(Color.WHITE, new Position(4, 4));
        board.setPiece(queen.getPosition(), queen);
    }

    static Stream<int[]> validMoveProvider() {
        return Stream.of(
            // Diagonal
            new int[]{2, 2}, new int[]{2, -2}, new int[]{-2, 2}, new int[]{-2, -2},
            // Straight
            new int[]{0, 2}, new int[]{0, -2}, new int[]{2, 0}, new int[]{-2, 0}
        );
    }

    @ParameterizedTest
    @MethodSource("validMoveProvider")
    void testValidMoves(int[] moveOffsets) {
        int dRow = moveOffsets[0];
        int dCol = moveOffsets[1];
        Position from = queen.getPosition();
        Position to = new Position(from.getRow() + dRow, from.getCol() + dCol);
        Move move = new Move(from, to, null);
        assertTrue(queen.canMove(board, move));
    }

    @Test
    void testInvalidMove_NotDiagonalOrStraight() {
        Move move = new Move(queen.getPosition(), new Position(5, 6), null); // L-shape like knight
        assertFalse(queen.canMove(board, move));
    }

    @Test
    void testInvalidMove_PieceInPath() {
        Pawn blockingPawn = new Pawn(Color.WHITE, new Position(5, 5));
        board.setPiece(blockingPawn.getPosition(), blockingPawn);
        Move move = new Move(queen.getPosition(), new Position(6, 6), null);
        assertFalse(queen.canMove(board, move));
    }

    @Test
    void testValidCapture() {
        Pawn opponentPawn = new Pawn(Color.BLACK, new Position(6, 6));
        board.setPiece(opponentPawn.getPosition(), opponentPawn);
        Move move = new Move(queen.getPosition(), opponentPawn.getPosition(), null);
        assertTrue(queen.canMove(board, move));
    }

    @Test
    void testInvalidCapture_SameColor() {
        Pawn sameColorPawn = new Pawn(Color.WHITE, new Position(6, 6));
        board.setPiece(sameColorPawn.getPosition(), sameColorPawn);
        Move move = new Move(queen.getPosition(), sameColorPawn.getPosition(), null);
        assertFalse(queen.canMove(board, move));
    }
}
