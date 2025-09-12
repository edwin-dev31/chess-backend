package com.chess.game.persistence.service.impl;

import com.chess.game.persistence.entity.GameEntity;
import com.chess.game.persistence.entity.MoveEntity;
import com.chess.game.persistence.entity.PlayerEntity;
import com.chess.game.persistence.repository.IGameRepository;
import com.chess.game.persistence.repository.IMoveRepository;
import com.chess.game.persistence.repository.IPlayerRepository;
import com.chess.game.persistence.service.interfaces.IMoveService;
import com.chess.game.presentation.dto.move.CreateMoveDTO;
import com.chess.game.presentation.dto.move.UpdateMoveDTO;
import com.chess.game.util.GameStatus;
import com.chess.game.util.exception.ResourceNotFoundException;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MoveService implements IMoveService {

    private final IMoveRepository moveRepository;
    private final IGameRepository gameRepository;
    private final IPlayerRepository playerRepository;

    public MoveService(IMoveRepository moveRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.moveRepository = moveRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public MoveEntity create(CreateMoveDTO dto, Long playerId) {
        GameEntity game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + dto.getGameId()));

        if (!game.getStatus().equals(GameStatus.PLAYING)) {
            throw new IllegalStateException("Game is not in playing status.");
        }

        Board board = new Board();
        board.loadFromFen(game.getFen());

        Side currentTurn = board.getSideToMove();
        PlayerEntity currentPlayer = (currentTurn == Side.WHITE) ? game.getWhitePlayer() : game.getBlackPlayer();

        if (!currentPlayer.getId().equals(playerId)) {
            throw new IllegalStateException("It's not your turn.");
        }

        Move move = new Move(Square.valueOf(dto.getFromSquare().toUpperCase()), Square.valueOf(dto.getToSquare().toUpperCase()));

        List<Move> legalMoves = board.legalMoves();
        if (legalMoves.stream().noneMatch(m -> m.getFrom().equals(move.getFrom()) && m.getTo().equals(move.getTo()))) {
            throw new IllegalArgumentException("Illegal move: " + dto.getFromSquare() + dto.getToSquare());
        }

        board.doMove(move);

        String san = move.getSan();

        MoveEntity moveEntity = MoveEntity.builder()
                .game(game)
                .player(currentPlayer)
                .moveNumber(board.getMoveCounter())
                .fromSquare(dto.getFromSquare())
                .toSquare(dto.getToSquare())
                .piece(board.getPiece(move.getTo()).getSanSymbol())
                .san(san)
                .fen(board.getFen())
                .createdAt(LocalDateTime.now())
                .build();

        game.setFen(board.getFen());

        String currentPgn = game.getPgn() == null ? "" : game.getPgn();
        String newPgn = currentPgn.isEmpty() ? "" : currentPgn + " ";
        if (currentTurn == Side.WHITE) {
            newPgn += board.getMoveCounter() + ". ";
        }
        newPgn += san;
        game.setPgn(newPgn);

        if (board.isMated()) {
            game.setStatus(GameStatus.FINISHED);
            game.setCurrentPlayer(currentPlayer); // The winner is the last one to move
        } else if (board.isStaleMate() || board.isDraw()) {
            game.setStatus(GameStatus.FINISHED);
            game.setCurrentPlayer(null); // No winner, so no current player
        } else {
            // The game is still in progress, switch the current player for the next turn.
            PlayerEntity nextPlayer = currentPlayer.getId().equals(game.getWhitePlayer().getId())
                ? game.getBlackPlayer()
                : game.getWhitePlayer();
            game.setCurrentPlayer(nextPlayer);
        }

        gameRepository.save(game);
        return moveRepository.save(moveEntity);
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
