package com.chess.game.domain;

import com.chess.game.util.enums.PieceType;
import com.github.bhlangonijr.chesslib.Side;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoveResult {
    private String newFen;
    private String sanMove;
    private PieceType movedPiece;
    private int moveNumber;
    private Side sideToMove;
    private MoveStatus moveStatus;
}
