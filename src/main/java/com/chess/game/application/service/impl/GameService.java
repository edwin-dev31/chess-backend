package com.chess.game.application.service.impl;

import com.chess.game.domain.ChessGameManager;
import com.chess.game.infrastructure.entity.GameEntity;
import com.chess.game.infrastructure.entity.MoveEntity;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.infrastructure.repository.IGameRepository;
import com.chess.game.infrastructure.repository.IMoveRepository;
import com.chess.game.infrastructure.repository.IPlayerRepository;
import com.chess.game.application.service.interfaces.IGameService;
import com.chess.game.application.dto.game.CreateGameDTO;
import com.chess.game.util.GameStatus;
import com.chess.game.util.exception.ResourceNotFoundException;
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
        PlayerEntity player1 = playerRepository.findById(dto.getBlackPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + dto.getBlackPlayerId()));
        PlayerEntity player2 = playerRepository.findById(dto.getWhitePlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + dto.getWhitePlayerId()));

        GameEntity newGame = GameEntity
                .builder()
                .blackPlayer(player1)
                .whitePlayer(player2)
                .currentPlayer(player2)
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
}
