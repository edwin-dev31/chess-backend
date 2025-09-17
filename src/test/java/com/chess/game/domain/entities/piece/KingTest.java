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

class KingTest {

    private Board board;
    private King king;

    @BeforeEach
    void setUp() {
        board = new Board();
        king = new King(Color.WHITE, new Position(4, 4));
        board.setPiece(king.getPosition(), king);
    }

    static Stream<int[]> validMoveProvider() {
        return Stream.of(
            new int[]{1, 0}, new int[]{-1, 0}, new int[]{0, 1}, new int[]{0, -1},
            new int[]{1, 1}, new int[]{1, -1}, new int[]{-1, 1}, new int[]{-1, -1}
        );
    }

    @ParameterizedTest
    @MethodSource("validMoveProvider")
    void testValidMoves(int[] moveOffsets) {
        int dRow = moveOffsets[0];
        int dCol = moveOffsets[1];
        Position from = king.getPosition();
        Position to = new Position(from.getRow() + dRow, from.getCol() + dCol);
        Move move = new Move(from, to, null);
        assertTrue(king.canMove(board, move));
    }

    @Test
    void testInvalidMove_MoreThanOneSquare() {
        Move move = new Move(king.getPosition(), new Position(6, 4), null);
        assertFalse(king.canMove(board, move));
    }

    @Test
    void testValidCapture() {
        Pawn opponentPawn = new Pawn(Color.BLACK, new Position(5, 5));
        board.setPiece(opponentPawn.getPosition(), opponentPawn);
        Move move = new Move(king.getPosition(), opponentPawn.getPosition(), null);
        assertTrue(king.canMove(board, move));
    }

    @Test
    void testInvalidCapture_SameColor() {
        Pawn sameColorPawn = new Pawn(Color.WHITE, new Position(5, 5));
        board.setPiece(sameColorPawn.getPosition(), sameColorPawn);
        Move move = new Move(king.getPosition(), sameColorPawn.getPosition(), null);
        assertFalse(king.canMove(board, move));
    }

    @Test
    void testCastle() {
        // TODO: Implement test once check detection is available
    }
}
