package com.chess.game.persistence.service.impl;

import com.chess.game.persistence.entity.MoveEntity;
import com.chess.game.persistence.entity.PlayerEntity;
import com.chess.game.persistence.repository.IGameRepository;
import com.chess.game.persistence.repository.IMoveRepository;
import com.chess.game.persistence.service.interfaces.IMoveService;
import com.chess.game.presentation.dto.move.CreateMoveDTO;
import com.chess.game.presentation.dto.move.UpdateMoveDTO;
import com.chess.game.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MoveService implements IMoveService {

    private final IMoveRepository moveRepository;
    private final IGameRepository gameRepository;

    public MoveService(IMoveRepository moveRepository, IGameRepository gameRepository) {
        this.moveRepository = moveRepository;
        this.gameRepository = gameRepository;
    }


    public MoveEntity create(CreateMoveDTO dto, PlayerEntity player) {
        // This is a simplified implementation. A real implementation would need to:
        // 1. Get the game from the database.
        // 2. Check if it's the correct player's turn.
        // 3. Create a Board object from the history of moves of the game.
        // 4. Validate the move using the Board and Piece classes.
        // 5. If the move is valid, create a new MoveEntity and save it.
        // 6. Update the game status if the game is over (checkmate, stalemate).
        var game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + dto.getGameId()));

        MoveEntity move = new MoveEntity();
        move.setGame(game);
        move.setPlayer(player);
        move.setFromSquare(dto.getFromSquare());
        move.setToSquare(dto.getToSquare());
        // Simplified move number
        move.setMoveNumber(moveRepository.findAllByGameId(dto.getGameId()).size() + 1);

        return moveRepository.save(move);
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
