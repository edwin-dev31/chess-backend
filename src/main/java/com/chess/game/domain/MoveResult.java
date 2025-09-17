package com.chess.game.domain;

import com.chess.game.util.enums.PieceType;
import com.github.bhlangonijr.chesslib.Side;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoveResult {
    String newFen;
    String sanMove;
    PieceType movedPiece;
    int moveNumber;
    Side sideToMove;
    GameEndStatus endStatus;
}
