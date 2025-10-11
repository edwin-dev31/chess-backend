package com.chess.game.presentation.socket;

import com.chess.game.application.dto.game.GameStatusDTO;
import com.chess.game.application.dto.game.MoveCreatedResponseDTO;
import com.chess.game.application.dto.move.CreateMoveDTO;
import com.chess.game.application.dto.move.MoveResponseDTO;
import com.chess.game.application.service.interfaces.IMoveService;
import com.chess.game.config.jwt.JwtUtil;
import com.chess.game.domain.HashidsUtil;
import com.chess.game.domain.MoveStatus;
import com.chess.game.infrastructure.entity.MoveEntity;
import com.chess.game.util.exception.IllegalStateExceptionCustom;
import com.chess.game.util.mapper.MoveMapper;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.security.Principal;
import java.util.Map;

@Controller
public class MoveSocketController {

    private final IMoveService moveService;
    private final MoveMapper moveMapper;
    private final JwtUtil jwt;
    private final SimpMessagingTemplate messagingTemplate;

    public MoveSocketController(IMoveService moveService, MoveMapper moveMapper, JwtUtil jwt, SimpMessagingTemplate messagingTemplate) {
        this.moveService = moveService;
        this.moveMapper = moveMapper;
        this.jwt = jwt;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/moves/{gameId}")
    @SendTo("/topic/moves/{gameId}")
    public MoveResponseDTO createMove(@Payload @Valid CreateMoveDTO dto,
                                      @Header("Authorization") String authHeader,
                                      @DestinationVariable String gameId) {

        String token = authHeader.substring(7);
        Long playerId = jwt.extractId(token);

        MoveCreatedResponseDTO createdMove = moveService.create(dto, playerId, HashidsUtil.decodeId(gameId));
        if (createdMove.getStatus() != MoveStatus.CONTINUES) {
            GameStatusDTO gameStatus = GameStatusDTO.builder()
                    .status(createdMove.getStatus())
                    .message(getEndGameMessage(createdMove.getStatus()))
                    .winnerName(createdMove.getWinnerName())
                    .isGameOver(true)
                    .build();

            messagingTemplate.convertAndSend(
                    "/topic/games/" + gameId + "/status",
                    gameStatus
            );
        }
        return moveMapper.mapTo(createdMove);
    }

    @MessageExceptionHandler
    public void handleException(IllegalStateExceptionCustom ex, StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        if (principal != null) {
            Map<String, String> error = Map.of("message", ex.getMessage());
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", error);
        }
    }

    private String getEndGameMessage(MoveStatus status) {
        return switch (status) {
            case CHECKMATE -> "Checkmate! The game has ended.";
            case STALEMATE -> "Stalemate! It's a draw.";
            case CHECK -> "Check!";
            default -> "";
        };
    }
}
