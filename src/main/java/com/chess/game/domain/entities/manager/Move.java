package com.chess.game.domain.entities.manager;

import com.chess.game.domain.entities.piece.Piece;
import lombok.Getter;

@Getter
public class Move {
    private final Position from;
    private final Position to;
    private final Piece promotionPiece;

    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;

    public Move(Position from, Position to, Piece promotionPiece) {
        this.from = from;
        this.to = to;
        this.promotionPiece = promotionPiece;

        this.fromRow = from.getRow();
        this.fromCol = from.getCol();
        this.toRow = to.getRow();
        this.toCol = to.getCol();
    }
}
