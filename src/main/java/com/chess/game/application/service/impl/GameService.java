package com.chess.game.application.service.impl;

import com.chess.game.application.dto.game.InvitationDto;
import com.chess.game.domain.ChessGameManager;
import com.chess.game.domain.HashidsUtil;
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
import com.chess.game.util.enums.PlayerStatus;
import com.chess.game.util.exception.IllegalStateExceptionCustom;
import com.chess.game.util.exception.ResourceNotFoundException;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
        PlayerEntity player2 = playerRepository.findById(dto.getBlackPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + dto.getBlackPlayerId()));

        GameEntity newGame = GameEntity
                .builder()
                .whitePlayer(player1)
                .blackPlayer(player2)
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
    public GameEntity startGame(Long gameId, String timeControl) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        if (game.getStatus() != GameStatus.WAITING) {
            throw new IllegalStateException("Game cannot be started, its current status is: " + game.getStatus());
        }
        game.setStatus(GameStatus.PLAYING);
        game.setTimeControl(timeControl);

        PlayerEntity player1 = playerRepository.findById(game.getWhitePlayer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + game.getWhitePlayer().getId()));
        PlayerEntity player2 = playerRepository.findById(game.getBlackPlayer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + game.getBlackPlayer().getId()));

        player1.setStatus(PlayerStatus.IN_GAME);
        player2.setStatus(PlayerStatus.IN_GAME);
        playerRepository.save(player1);
        playerRepository.save(player2);

        return gameRepository.save(game);
    }

    @Override
    public String getCurrentPlayerColor(Long gameId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        if (!game.getStatus().equals(GameStatus.PLAYING)) {
            throw new IllegalStateExceptionCustom("Game is not in playing status.");
        }

        Side currentTurn = game.getFen().contains(" w ") ? Side.WHITE : Side.BLACK;

        return currentTurn.name();
    }

    @Override
    public Long getOpponentId(Long gameId, Long playerId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        Long playerIdWhite = game.getWhitePlayer().getId();
        Long playerIdBlack = game.getBlackPlayer().getId();

        if (!playerId.equals(playerIdWhite) && !playerId.equals(playerIdBlack)) {
            throw new ResourceNotFoundException("Player not found in this game");
        }

        return playerId.equals(playerIdWhite) ? playerIdBlack : playerIdWhite;
    }

    @Override
    public InvitationDto createInvitation(Long fromPlayerId, Long toUserId) {
        PlayerEntity player = playerRepository.findById(fromPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + fromPlayerId));
        return InvitationDto
                .builder()
                .fromUserId(fromPlayerId)
                .fromUsername(player.getUsername())
                .toUserId(toUserId)
                .status(Invitation.PENDING)
                .build();
    }

    @Override
    public InvitationDto acceptInvitation(Long fromPlayerId, Long toUserId) {
        PlayerEntity player1 = playerRepository.findById(fromPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + fromPlayerId));

        PlayerEntity player2 = playerRepository.findById(toUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + toUserId));

        Long IdWhite, Idblack;
        int randomNum = ThreadLocalRandom.current().nextInt(2);

        if (randomNum == 1) {
            IdWhite = fromPlayerId;
            Idblack = toUserId;
        } else  {
            IdWhite = toUserId;
            Idblack = fromPlayerId;
        }

        CreateGameDTO createGameDTO = new CreateGameDTO(IdWhite, Idblack, "");
        GameEntity newGame = createGame(createGameDTO);

        return InvitationDto
                .builder()
                .code(HashidsUtil.encodeId(newGame.getId()))
                .fromUserId(fromPlayerId)
                .fromUsername(player1.getUsername())
                .toUserId(toUserId)
                .toUsername(player2.getUsername())
                .status(Invitation.ACCEPTED)
                .build();
    }

    @Override
    public InvitationDto rejectInvitation(Long fromPlayerId, Long toUserId) {
        PlayerEntity player1 = playerRepository.findById(fromPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + fromPlayerId));

        PlayerEntity player2 = playerRepository.findById(toUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + toUserId));

        return InvitationDto
                .builder()
                .fromUserId(fromPlayerId)
                .fromUsername(player1.getUsername())
                .toUserId(toUserId)
                .toUsername(player2.getUsername())
                .status(Invitation.REJECTED)
                .build();
    }

    @Override
    public String leaveGame(Long gameId, Long playerId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        PlayerEntity player = playerRepository.findById(getOpponentId(gameId, playerId))
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with this game"));

        game.setStatus(GameStatus.FINISHED);
        game.setFinishedAt(LocalDateTime.now());
        game.setCurrentPlayer(player);

        gameRepository.save(game);
        return "You leave the game successfull";
    }
}
