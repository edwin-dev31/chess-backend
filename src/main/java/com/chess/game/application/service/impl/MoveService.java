package com.chess.game.application.service.impl;

import com.chess.game.application.dto.game.MoveCreatedResponseDTO;
import com.chess.game.application.dto.move.CreateMoveDTO;
import com.chess.game.application.dto.move.UpdateMoveDTO;
import com.chess.game.application.service.interfaces.IMoveService;
import com.chess.game.domain.ChessGameManager;
import com.chess.game.domain.MoveResult;
import com.chess.game.domain.MoveStatus;
import com.chess.game.infrastructure.entity.GameEntity;
import com.chess.game.infrastructure.entity.MoveEntity;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.infrastructure.repository.IGameRepository;
import com.chess.game.infrastructure.repository.IMoveRepository;
import com.chess.game.util.enums.GameStatus;
import com.chess.game.util.exception.IllegalStateExceptionCustom;
import com.chess.game.util.exception.ResourceNotFoundException;
import com.github.bhlangonijr.chesslib.Side;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MoveService implements IMoveService {

    private final IMoveRepository moveRepository;
    private final IGameRepository gameRepository;
    private final ChessGameManager chessGameManager;

    public MoveService(IMoveRepository moveRepository, IGameRepository gameRepository, ChessGameManager chessGameManager) {
        this.moveRepository = moveRepository;
        this.gameRepository = gameRepository;
        this.chessGameManager = chessGameManager;
    }

    @Override
    @Transactional
    public MoveCreatedResponseDTO create(CreateMoveDTO dto, Long playerId, Long gameId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        if (!game.getStatus().equals(GameStatus.PLAYING)) {
            throw new IllegalStateExceptionCustom("Game is not in playing status.");
        }

        Side currentTurn = game.getFen().contains(" w ") ? Side.WHITE : Side.BLACK;
        PlayerEntity currentPlayer = (currentTurn == Side.WHITE) ? game.getWhitePlayer() : game.getBlackPlayer();

        if (!currentPlayer.getId().equals(playerId)) {
            throw new IllegalStateExceptionCustom("It's not your turn.");
        }

        MoveResult moveResult = chessGameManager.makeMove(game.getFen(), dto.getFromSquare(), dto.getToSquare());

        MoveEntity moveEntity = MoveEntity.builder()
                .game(game)
                .player(currentPlayer)
                .moveNumber(moveResult.getMoveNumber())
                .fromSquare(dto.getFromSquare())
                .toSquare(dto.getToSquare())
                .piece(moveResult.getMovedPiece())
                .san(moveResult.getSanMove())
                .fen(moveResult.getNewFen())
                .createdAt(LocalDateTime.now())
                .build();

        game.setFen(moveResult.getNewFen());
        updatePgn(game, moveResult.getSanMove(), currentTurn);
        updateGameStatus(game, moveResult.getMoveStatus(), currentPlayer);

        gameRepository.save(game);
        MoveEntity entity = moveRepository.save(moveEntity);
        return MoveCreatedResponseDTO.builder()
                .moveNumber(entity.getMoveNumber())
                .fromSquare(entity.getFromSquare())
                .toSquare(entity.getToSquare())
                .san(entity.getSan())
                .fen(entity.getFen())
                .status(moveResult.getMoveStatus())
                .winnerName((moveResult.getMoveStatus() == MoveStatus.CHECKMATE) ? currentPlayer.getUsername() : null)
                .build();
    }

    private void updatePgn(GameEntity game, String sanMove, Side currentTurn) {

    }

    private void updateGameStatus(GameEntity game, MoveStatus endStatus, PlayerEntity playerWhoMoved) {
        if (endStatus == MoveStatus.CHECKMATE) {
            game.setStatus(GameStatus.FINISHED);
            game.setCurrentPlayer(playerWhoMoved);
            game.setFinishedAt(LocalDateTime.now());
        } else if (endStatus == MoveStatus.STALEMATE) {
            game.setStatus(GameStatus.FINISHED);
            game.setCurrentPlayer(null);
            game.setFinishedAt(LocalDateTime.now());
        } else {
            PlayerEntity nextPlayer = playerWhoMoved.getId().equals(game.getWhitePlayer().getId())
                ? game.getBlackPlayer()
                : game.getWhitePlayer();
            game.setCurrentPlayer(nextPlayer);
        }
    }

    @Override
    public List<MoveEntity> findAllByGameId(Long gameId) {
        return moveRepository.findAllByGameId(gameId);
    }

    @Override
    public Optional<MoveEntity> findById(Long id) {
        return moveRepository.findById(id);
    }

    @Override
    public MoveEntity update(Long id, UpdateMoveDTO dto) {
        MoveEntity move = moveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Move not found with id: " + id));

        if (dto.getFromSquare() != null) {
            move.setFromSquare(dto.getFromSquare());
        }
        if (dto.getToSquare() != null) {
            move.setToSquare(dto.getToSquare());
        }

        return moveRepository.save(move);
    }

    @Override
    public void deleteById(Long id) {
        if (!moveRepository.existsById(id)) {
            throw new ResourceNotFoundException("Move not found with id: " + id);
        }
        moveRepository.deleteById(id);
    }
}
