package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.Board;
import com.chess.game.domain.entities.manager.Move;
import com.chess.game.domain.entities.manager.Position;
import com.chess.game.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RookTest {

    private Board board;
    private Rook whiteRook;
    private Rook blackRook;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testWhiteRook_ValidHorizontalMove() {
        whiteRook = new Rook(Color.WHITE, new Position(1, 1));
        board.setPiece(whiteRook.getPosition(), whiteRook);
        Move move = new Move(new Position(1, 1), new Position(1, 5), null);
        assertTrue(whiteRook.canMove(board, move));
    }

    @Test
    void testWhiteRook_ValidVerticalMove() {
        whiteRook = new Rook(Color.WHITE, new Position(1, 1));
        board.setPiece(whiteRook.getPosition(), whiteRook);
        Move move = new Move(new Position(1, 1), new Position(5, 1), null);
        assertTrue(whiteRook.canMove(board, move));
    }

    @Test
    void testBlackRook_ValidHorizontalMove() {
        blackRook = new Rook(Color.BLACK, new Position(8, 8));
        board.setPiece(blackRook.getPosition(), blackRook);
        Move move = new Move(new Position(8, 8), new Position(8, 4), null);
        assertTrue(blackRook.canMove(board, move));
    }

    @Test
    void testBlackRook_ValidVerticalMove() {
        blackRook = new Rook(Color.BLACK, new Position(8, 8));
        board.setPiece(blackRook.getPosition(), blackRook);
        Move move = new Move(new Position(8, 8), new Position(4, 8), null);
        assertTrue(blackRook.canMove(board, move));
    }

    @Test
    void test_InvalidDiagonalMove() {
        whiteRook = new Rook(Color.WHITE, new Position(1, 1));
        board.setPiece(whiteRook.getPosition(), whiteRook);
        Move move = new Move(new Position(1, 1), new Position(4, 4), null);
        assertFalse(whiteRook.canMove(board, move));
    }

    @Test
    void test_InvalidMove_PieceInPath() {
        whiteRook = new Rook(Color.WHITE, new Position(1, 1));
        Pawn blockingPawn = new Pawn(Color.WHITE, new Position(1, 3));
        board.setPiece(whiteRook.getPosition(), whiteRook);
        board.setPiece(blockingPawn.getPosition(), blockingPawn);
        Move move = new Move(new Position(1, 1), new Position(1, 5), null);
        assertFalse(whiteRook.canMove(board, move));
    }

    @Test
    void test_ValidCapture() {
        whiteRook = new Rook(Color.WHITE, new Position(1, 1));
        Pawn opponentPawn = new Pawn(Color.BLACK, new Position(1, 5));
        board.setPiece(whiteRook.getPosition(), whiteRook);
        board.setPiece(opponentPawn.getPosition(), opponentPawn);
        Move move = new Move(new Position(1, 1), new Position(1, 5), null);
        assertTrue(whiteRook.canMove(board, move));
    }

    @Test
    void test_InvalidCapture_SameColor() {
        whiteRook = new Rook(Color.WHITE, new Position(1, 1));
        Pawn sameColorPawn = new Pawn(Color.WHITE, new Position(1, 5));
        board.setPiece(whiteRook.getPosition(), whiteRook);
        board.setPiece(sameColorPawn.getPosition(), sameColorPawn);
        Move move = new Move(new Position(1, 1), new Position(1, 5), null);
        assertFalse(whiteRook.canMove(board, move));
    }
}
