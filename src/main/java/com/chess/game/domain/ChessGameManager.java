package com.chess.game.domain;

import com.chess.game.infrastructure.entity.GameEntity;
import com.chess.game.infrastructure.entity.MoveEntity;
import com.chess.game.util.enums.PieceType;
import com.chess.game.util.exception.IllegalStateExceptionCustom;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import org.springframework.stereotype.Component;
import java.util.Comparator;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ChessGameManager {

    public static String getInitialFen() {
        Board board = new Board();
        return board.getFen();
    }

    public MoveResult makeMove(String fen, String fromSquare, String toSquare) {
        Board board = new Board();
        board.loadFromFen(fen);

        Move moveInput = new Move(
                Square.valueOf(fromSquare.toUpperCase()),
                Square.valueOf(toSquare.toUpperCase())
        );

        MoveStatus moveStatus = MoveStatus.CONTINUES;

        try {
            List<Move> legalMoves = MoveGenerator.generateLegalMoves(board);
            List<Move> pseudoMoves = MoveGenerator.generatePseudoLegalMoves(board);

            System.out.println("legal moves: " + legalMoves);
            if (legalMoves.isEmpty()) {
                if (board.isKingAttacked()) {
                    throw new IllegalStateExceptionCustom("Checkmate! The game is over.");
                } else {
                    throw new IllegalStateExceptionCustom("Stalemate! It's a draw.");
                }
            }

            boolean isPseudo = pseudoMoves.stream()
                    .anyMatch(m -> m.getFrom().equals(moveInput.getFrom())
                            && m.getTo().equals(moveInput.getTo()));

            if (!isPseudo) {
                throw new IllegalStateExceptionCustom("Illegal move: Piece cannot move that way.");
            }

            Move move = legalMoves.stream()
                    .filter(m -> m.getFrom().equals(moveInput.getFrom())
                            && m.getTo().equals(moveInput.getTo()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateExceptionCustom(
                            "Illegal move: This move would leave your king in check."));

            board.doMove(move);

            if (board.isMated()) {
                moveStatus = MoveStatus.CHECKMATE;
            } else if (board.isKingAttacked()) {
                moveStatus = MoveStatus.CHECK;
            } else if (board.isStaleMate() || board.isDraw() || board.isRepetition() || board.getHalfMoveCounter() >= 100) {
                moveStatus = MoveStatus.STALEMATE;
            }

            Piece movedPiece = board.getPiece(move.getTo());
            PieceType pieceType = PieceType.valueOf(movedPiece.getPieceType().name());

            return MoveResult.builder()
                    .newFen(board.getFen())
                    .sanMove(move.toString())
                    .movedPiece(pieceType)
                    .moveNumber(board.getMoveCounter())
                    .sideToMove(board.getSideToMove())
                    .moveStatus(moveStatus)
                    .build();

        } catch (MoveGeneratorException e) {
            throw new IllegalStateExceptionCustom("Could not generate legal moves: " + e.getMessage());
        }
    }

    public static String buildPgn(GameEntity game, List<MoveEntity> moves) {
        StringBuilder pgnBuilder = new StringBuilder();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        String result = "*";

        pgnBuilder.append("[Event \"Casual Game\"]\n")
                .append("[Site \"Local\"]\n")
                .append("[Date \"")
                .append(game.getCreatedAt().format(dateFormat))
                .append("\"]\n")
                .append("[Round \"-\"]\n")
                .append("[White \"")
                .append(game.getWhitePlayer() != null ? game.getWhitePlayer().getUsername() : "Unknown")
                .append("\"]\n")
                .append("[Black \"")
                .append(game.getBlackPlayer() != null ? game.getBlackPlayer().getUsername() : "Unknown")
                .append("\"]\n")
                .append("[Result \"").append(result).append("\"]\n\n");

        moves.sort(Comparator.comparingInt(MoveEntity::getMoveNumber)
                .thenComparing(MoveEntity::getCreatedAt));

        Board board = new Board();
        int moveNumber = 1;

        for (int i = 0; i < moves.size(); i++) {
            MoveEntity m = moves.get(i);

            Square from = Square.valueOf(m.getFromSquare().toUpperCase());
            Square to = Square.valueOf(m.getToSquare().toUpperCase());

            Move move = new Move(from, to);
            String san = simpleSanFallback(m);
            m.setSan(san);

            if (i % 2 == 0) {
                pgnBuilder.append(moveNumber).append(". ");
            }
            pgnBuilder.append(san).append(" ");
            board.doMove(move);

            if (i % 2 != 0) {
                moveNumber++;
            }
        }

        pgnBuilder.append(result);
        return pgnBuilder.toString().trim();
    }

    private static String simpleSanFallback(MoveEntity m) {
        String pieceSymbol;
        switch (m.getPiece()) {
            case KNIGHT -> pieceSymbol = "N";
            case BISHOP -> pieceSymbol = "B";
            case ROOK -> pieceSymbol = "R";
            case QUEEN -> pieceSymbol = "Q";
            case KING -> pieceSymbol = "K";
            default -> pieceSymbol = "";
        }
        return pieceSymbol + m.getToSquare().toLowerCase();
    }
}