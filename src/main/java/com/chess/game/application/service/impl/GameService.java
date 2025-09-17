package com.chess.game.application.service.impl;

import com.chess.game.application.dto.game.InvitationDto;
import com.chess.game.domain.ChessGameManager;
import com.chess.game.infrastructure.entity.GameEntity;
import com.chess.game.infrastructure.entity.MoveEntity;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.infrastructure.repository.IGameRepository;
import com.chess.game.infrastructure.repository.IMoveRepository;
import com.chess.game.infrastructure.repository.IPlayerRepository;
import com.chess.game.application.service.interfaces.IGameService;
import com.chess.game.application.dto.game.CreateGameDTO;
import com.chess.game.util.enums.GameStatus;
import com.chess.game.util.enums.Invitation;
import com.chess.game.util.exception.IllegalStateExceptionCustom;
import com.chess.game.util.exception.ResourceNotFoundException;
import com.github.bhlangonijr.chesslib.Side;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GameService implements IGameService {

    private final IGameRepository gameRepository;
    private final IPlayerRepository playerRepository;
    private final IMoveRepository moveRepository;

    public GameService(IGameRepository gameRepository, IPlayerRepository playerRepository, IMoveRepository moveRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.moveRepository = moveRepository;
    }

    @Override
    public GameEntity createGame(CreateGameDTO dto) {
        PlayerEntity player1 = playerRepository.findById(dto.getWhitePlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + dto.getWhitePlayerId()));

        GameEntity newGame = GameEntity
                .builder()
                .whitePlayer(player1)
                .currentPlayer(player1)
                .status(GameStatus.WAITING)
                .timeControl(dto.getTimeControl())
                .createdAt(LocalDateTime.now())
                .fen(generateInitFen())
                .build();

        return gameRepository.save(newGame);
    }

    private String generateInitFen() {
        return ChessGameManager.getInitialFen();
    }
    @Override
    public List<GameEntity> findAll() {
        return gameRepository.findAll();
    }

    @Override
    public Optional<GameEntity> findById(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new ResourceNotFoundException("Game not found with id: " + id);
        }
        gameRepository.deleteById(id);
    }

    @Override
    public String getFenByGameId(Long gameId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
        return game.getFen();
    }

    @Override
    public String getPgnByGameId(Long gameId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
        List<MoveEntity> moves = moveRepository.findAllByGameIdOrderByMoveNumberAsc(gameId);
        String pgn = ChessGameManager.buildPgn(game, moves);
        System.out.println("PGN \n"+pgn);
        game.setPgn(pgn);
        GameEntity saved = gameRepository.save(game);
        return saved.getPgn();
    }

    @Override
    public GameEntity startGame(Long gameId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        if (game.getStatus() != GameStatus.WAITING) {
            throw new IllegalStateException("Game cannot be started, its current status is: " + game.getStatus());
        }
        game.setStatus(GameStatus.PLAYING);

        return gameRepository.save(game);
    }

    @Override
    public String getCurrentPlayerColor(Long gameId) {
//        GameEntity game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
//
//        if (!game.getStatus().equals(GameStatus.PLAYING)) {
//            throw new IllegalStateExceptionCustom("Game is not in playing status.");
//        }
//
//        Side currentTurn = game.getFen().contains(" w ") ? Side.WHITE : Side.BLACK;
//
//        return currentTurn.name();
        return "WRITE";
    }

    @Override
    public InvitationDto createInvitation(Long gameId, Long fromPlayerId, Long toUserId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
        if(game.getWhitePlayer().getId() != fromPlayerId)
            throw new IllegalStateExceptionCustom("You are not the creator");

        return new InvitationDto(gameId, fromPlayerId, toUserId, Invitation.PENDING);
    }

    @Override
    public InvitationDto acceptInvitation(Long gameId, Long playerId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
        if (!game.getStatus().equals(GameStatus.FINISHED) || game.getStatus().equals(GameStatus.NOT_ACCEPTED)) {
            throw new IllegalStateExceptionCustom("Game is not able to play.");
        }
        game.setStatus(GameStatus.WAITING);
        gameRepository.save(game);

        return new InvitationDto(gameId, game.getWhitePlayer().getId(), playerId, Invitation.ACCEPTED);
    }

    @Override
    public InvitationDto rejectInvitation(Long gameId, Long playerId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
        game.setStatus(GameStatus.NOT_ACCEPTED);
        game.setFinishedAt(LocalDateTime.now());
        gameRepository.save(game);

        return new InvitationDto(gameId, game.getWhitePlayer().getId(), playerId, Invitation.REJECTED);
    }
}
