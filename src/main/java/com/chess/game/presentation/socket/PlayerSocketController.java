package com.chess.game.presentation.socket;

import com.chess.game.application.service.interfaces.IPlayerService;
import com.chess.game.domain.HashidsUtil;
import com.chess.game.util.enums.PlayerStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class PlayerSocketController {
    private final IPlayerService service;

    public PlayerSocketController(IPlayerService service) {
        this.service = service;
    }

    @MessageMapping("/players/{playerId}/status")
    @SendTo("/topic/games/{playerId}/status")
    public Map<String, PlayerStatus> getCurrentPlayerColor(@DestinationVariable Long playerId) {
        PlayerStatus status = service.getPlayerStatus(playerId);

        return Map.of("status", status);
    }
}
