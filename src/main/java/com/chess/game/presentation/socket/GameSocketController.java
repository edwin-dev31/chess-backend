package com.chess.game.presentation.socket;

import com.chess.game.application.service.interfaces.IGameService;
import com.chess.game.domain.HashidsUtil;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class GameSocketController {

    private final IGameService gameService;

    public GameSocketController(IGameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/games/{gameId}/fen")
    @SendTo("/topic/games/{gameId}/fen")
    public Map<String, String> getFen(@DestinationVariable String gameId) {
        String fen = gameService.getFenByGameId(HashidsUtil.decodeId(gameId));
        return Map.of("fen", fen, "gameId", gameId.toString());
    }

    @MessageMapping("/games/{gameId}/color")
    @SendTo("/topic/games/{gameId}/color")
    public Map<String, String> getCurrentPlayerColor(@DestinationVariable String gameId) {
        String color = gameService.getCurrentPlayerColor(HashidsUtil.decodeId(gameId));
        return Map.of("color", color, "gameId", gameId.toString());
    }
}
