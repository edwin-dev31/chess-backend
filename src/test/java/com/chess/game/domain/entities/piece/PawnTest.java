package com.chess.game.domain.entities.piece;

import com.chess.game.domain.entities.manager.Board;
import com.chess.game.domain.entities.manager.Move;
import com.chess.game.domain.entities.manager.Position;
import com.chess.game.util.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    private Board board;
    private Pawn whitePawn;
    private Pawn blackPawn;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testWhitePawn_ValidSingleStep() {
        whitePawn = new Pawn(Color.WHITE, new Position(2, 4));
        board.setPiece(whitePawn.getPosition(), whitePawn);
        Move move = new Move(new Position(2, 4), new Position(3, 4), null);
        assertTrue(whitePawn.canMove(board, move));
    }

    @Test
    void testWhitePawn_ValidDoubleStep_FromStart() {
        whitePawn = new Pawn(Color.WHITE, new Position(2, 4));
        board.setPiece(whitePawn.getPosition(), whitePawn);
        Move move = new Move(new Position(2, 4), new Position(4, 4), null);
        assertTrue(whitePawn.canMove(board, move));
    }

    @Test
    void testWhitePawn_InvalidDoubleStep_NotFromStart() {
        whitePawn = new Pawn(Color.WHITE, new Position(3, 4));
        board.setPiece(whitePawn.getPosition(), whitePawn);
        Move move = new Move(new Position(3, 4), new Position(5, 4), null);
        assertFalse(whitePawn.canMove(board, move));
    }
    
    @Test
    void testBlackPawn_ValidSingleStep() {
        blackPawn = new Pawn(Color.BLACK, new Position(7, 4));
        board.setPiece(blackPawn.getPosition(), blackPawn);
        Move move = new Move(new Position(7, 4), new Position(6, 4), null);
        assertTrue(blackPawn.canMove(board, move));
    }

    @Test
    void testBlackPawn_ValidDoubleStep_FromStart() {
        blackPawn = new Pawn(Color.BLACK, new Position(7, 4));
        board.setPiece(blackPawn.getPosition(), blackPawn);
        Move move = new Move(new Position(7, 4), new Position(5, 4), null);
        assertTrue(blackPawn.canMove(board, move));
    }

    @Test
    void testWhitePawn_ValidCapture() {
        whitePawn = new Pawn(Color.WHITE, new Position(2, 4));
        blackPawn = new Pawn(Color.BLACK, new Position(3, 5));
        board.setPiece(whitePawn.getPosition(), whitePawn);
        board.setPiece(blackPawn.getPosition(), blackPawn);
        Move move = new Move(new Position(2, 4), new Position(3, 5), null);
        assertTrue(whitePawn.canMove(board, move));
    }

    @Test
    void testBlackPawn_ValidCapture() {
        blackPawn = new Pawn(Color.BLACK, new Position(7, 4));
        whitePawn = new Pawn(Color.WHITE, new Position(6, 3));
        board.setPiece(blackPawn.getPosition(), blackPawn);
        board.setPiece(whitePawn.getPosition(), whitePawn);
        Move move = new Move(new Position(7, 4), new Position(6, 3), null);
        assertTrue(blackPawn.canMove(board, move));
    }

    @Test
    void testWhitePawn_InvalidCapture_EmptySquare() {
        whitePawn = new Pawn(Color.WHITE, new Position(2, 4));
        board.setPiece(whitePawn.getPosition(), whitePawn);
        Move move = new Move(new Position(2, 4), new Position(3, 5), null);
        assertFalse(whitePawn.canMove(board, move));
    }

    @Test
    void testWhitePawn_InvalidMove_ForwardWithPieceInFront() {
        whitePawn = new Pawn(Color.WHITE, new Position(2, 4));
        blackPawn = new Pawn(Color.BLACK, new Position(3, 4));
        board.setPiece(whitePawn.getPosition(), whitePawn);
        board.setPiece(blackPawn.getPosition(), blackPawn);
        Move move = new Move(new Position(2, 4), new Position(3, 4), null);
        assertFalse(whitePawn.canMove(board, move));
    }
}
