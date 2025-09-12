package com.chess.game.domain.entities.manager;

import com.chess.game.domain.entities.piece.Piece;

public class Board {
    private Piece[][] squares = new Piece[8][8];

    public Piece getPiece(Position pos) {
        return squares[pos.getRow() - 1 ][pos.getCol() -1];
    }

    public void setPiece(Position pos, Piece piece) {
        squares[pos.getRow()-1][pos.getCol()-1] = piece;
        if (piece != null) piece.setPosition(pos);
    }

    public boolean applyMove(Move move) {
        Piece piece = getPiece(move.getFrom());
        if (piece != null && piece.canMove(this, move)) {
            setPiece(move.getTo(), piece);
            setPiece(move.getFrom(), null);
            return true;
        }
        return false;
    }
}
