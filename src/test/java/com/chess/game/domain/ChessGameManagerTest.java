package com.chess.game.domain;

import com.chess.game.util.exception.IllegalStateExceptionCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameManagerTest {

    private ChessGameManager chessGameManager;

    @BeforeEach
    void setUp() {
        chessGameManager = new ChessGameManager();
    }

    @Test
    void testInitBoard(){
        String initBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        assertEquals(initBoard, ChessGameManager.getInitialFen());
    }

    @Test
    void testMakeValidMove() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        MoveResult result = chessGameManager.makeMove(initialFen, "e2", "e4");

        assertNotNull(result);
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", result.getNewFen());
    }

    @Test
    void testMakeInvalidMove() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        Exception exception = assertThrows(IllegalStateExceptionCustom.class, () -> {
            chessGameManager.makeMove(initialFen, "e2", "e5");
        });

        String expectedMessage = "Illegal move: Piece cannot move that way.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMoveThatPutsKingInCheck() {
        String fen = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2";
        chessGameManager.makeMove(fen, "d1", "h5");

        String fen2 = "rnbqkbnr/pppp1ppp/8/4p2Q/4P3/8/PPPP1PPP/RNB1KBNR b KQkq - 1 2";
        MoveResult result = chessGameManager.makeMove(fen2, "g7", "g6");

        assertNotNull(result);
    }

    @Test
    void testCheckmate() {
        String checkmateFen = "rnb1k1nr/pppp1ppp/8/2b1P3/6P1/2N5/PPPPPq1P/R1BQKBNR w KQkq - 1 7";

        IllegalStateExceptionCustom exception = assertThrows(
                IllegalStateExceptionCustom.class,
                () -> chessGameManager.makeMove(checkmateFen, "e1", "f2")
        );

        assertEquals("Checkmate! The game is over.", exception.getMessage());
    }

    @Test
    void testCheckStalemateWithOutValidMoves(){
        String fen = "4k1K1/7R/8/8/8/8/3Q4/8 b - - 0 1";

        IllegalStateExceptionCustom exception = assertThrows(
                IllegalStateExceptionCustom.class,
                () -> chessGameManager.makeMove(fen, "c6", "b6")
        );

        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("Stalemate! It's a draw."));
    }

    @Test
    void testStalemateWithValidMoves() {
        String fen = "8/8/8/8/8/6k1/5q2/7K w - - 0 1";

        IllegalStateExceptionCustom exception = assertThrows(
                IllegalStateExceptionCustom.class,
                () -> chessGameManager.makeMove(fen, "c6", "b6")
        );

        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("Stalemate! It's a draw."));
    }

    @Test
    void testSeudoMove(){
        String fen = "2k5/8/3r4/8/8/8/3r4/4K3 w - - 0 1";

        IllegalStateExceptionCustom exception = assertThrows(
                IllegalStateExceptionCustom.class,
                () -> chessGameManager.makeMove(fen, "c6", "b6")
        );

        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("Illegal move: Piece cannot move that way."));
    }

    @Test
    void testIllegalMoveKingInCheck() {
        String fen = "rnb1kbnr/pppp1ppp/8/4p3/4P3/8/PPPPqPPP/RNB1KBNR w KQkq - 0 3";

        IllegalStateExceptionCustom exception = assertThrows(
                IllegalStateExceptionCustom.class,
                () -> chessGameManager.makeMove(fen, "f2", "f4")
        );

        assertTrue(exception.getMessage().contains("Illegal move: This move would leave your king in check."));

    }


}
