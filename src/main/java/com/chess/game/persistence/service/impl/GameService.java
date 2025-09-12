package com.chess.game.persistence.service.impl;

import com.chess.game.persistence.entity.GameEntity;
import com.chess.game.persistence.entity.PlayerEntity;
import com.chess.game.persistence.repository.IGameRepository;
import com.chess.game.persistence.repository.IPlayerRepository;
import com.chess.game.persistence.service.interfaces.IGameService;
import com.chess.game.presentation.dto.game.CreateGameDTO;
import com.chess.game.util.GameStatus;
import com.chess.game.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GameService implements IGameService {

    private final IGameRepository gameRepository;
    private final IPlayerRepository playerRepository;

    public GameService(IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public GameEntity createGame(CreateGameDTO dto) {
        PlayerEntity player1 = playerRepository.findById(dto.getBlackPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + dto.getBlackPlayerId()));
        PlayerEntity player2 = playerRepository.findById(dto.getWhitePlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + dto.getWhitePlayerId()));
        System.out.println("Holaaaaaaaaaaaaa      ");
        GameEntity newGame = GameEntity
                .builder()
                .blackPlayer(player1)
                .whitePlayer(player2)
                .status(GameStatus.WAITING)
                .timeControl(dto.getTimeControl())
                .createdAt(LocalDateTime.now())
                .fen(generateInitFen())
                .build();

        return gameRepository.save(newGame);
    }

    private String generateInitFen() {
        return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
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
        return game.getPgn();
    }
}
