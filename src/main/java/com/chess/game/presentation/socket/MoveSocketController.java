package com.chess.game.presentation.socket;

import com.chess.game.application.dto.move.CreateMoveDTO;
import com.chess.game.application.dto.move.MoveResponseDTO;
import com.chess.game.application.service.interfaces.IMoveService;
import com.chess.game.config.jwt.JwtUtil;
import com.chess.game.infrastructure.entity.MoveEntity;
import com.chess.game.util.mapper.MoveMapper;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MoveSocketController {

    private final IMoveService moveService;
    private final MoveMapper moveMapper;
    private final JwtUtil jwt;

    public MoveSocketController(IMoveService moveService, MoveMapper moveMapper, JwtUtil jwt) {
        this.moveService = moveService;
        this.moveMapper = moveMapper;
        this.jwt = jwt;
    }

    @MessageMapping("/moves/{gameId}")
    @SendTo("/topic/moves/{gameId}")
    public MoveResponseDTO createMove(@Payload @Valid CreateMoveDTO dto,
                                      @Header("Authorization") String authHeader,
                                      @DestinationVariable Long gameId) {

        String token = authHeader.substring(7);
        Long playerId = jwt.extractId(token);

        MoveEntity createdMove = moveService.create(dto, playerId, gameId);
        return moveMapper.mapTo(createdMove);
    }
}
