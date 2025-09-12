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

class KnightTest {

    private Board board;
    private Knight knight;

    @BeforeEach
    void setUp() {
        board = new Board();
        knight = new Knight(Color.WHITE, new Position(4, 4));
        board.setPiece(knight.getPosition(), knight);
    }

    static Stream<int[]> validMoveProvider() {
        return Stream.of(
            new int[]{2, 1}, new int[]{2, -1}, new int[]{-2, 1}, new int[]{-2, -1},
            new int[]{1, 2}, new int[]{1, -2}, new int[]{-1, 2}, new int[]{-1, -2}
        );
    }

    @ParameterizedTest
    @MethodSource("validMoveProvider")
    void testValidMoves(int[] moveOffsets) {
        int dRow = moveOffsets[0];
        int dCol = moveOffsets[1];
        Position from = knight.getPosition();
        Position to = new Position(from.getRow() + dRow, from.getCol() + dCol);
        Move move = new Move(from, to, null);
        assertTrue(knight.canMove(board, move));
    }

    @Test
    void testInvalidMove_NotLShape() {
        Move move = new Move(knight.getPosition(), new Position(5, 5), null);
        assertFalse(knight.canMove(board, move));
    }

    @Test
    void testValidCapture() {
        Pawn opponentPawn = new Pawn(Color.BLACK, new Position(6, 5));
        board.setPiece(opponentPawn.getPosition(), opponentPawn);
        Move move = new Move(knight.getPosition(), opponentPawn.getPosition(), null);
        assertTrue(knight.canMove(board, move));
    }

    @Test
    void testInvalidCapture_SameColor() {
        Pawn sameColorPawn = new Pawn(Color.WHITE, new Position(6, 5));
        board.setPiece(sameColorPawn.getPosition(), sameColorPawn);
        Move move = new Move(knight.getPosition(), sameColorPawn.getPosition(), null);
        assertFalse(knight.canMove(board, move));
    }
}
