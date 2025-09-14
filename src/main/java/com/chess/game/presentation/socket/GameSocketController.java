package com.chess.game.presentation.socket;

import com.chess.game.application.service.interfaces.IGameService;
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
    public Map<String, String> getFen(@DestinationVariable Long gameId) {
        String fen = gameService.getFenByGameId(gameId);
        return Map.of("fen", fen, "gameId", gameId.toString());
    }

    @MessageMapping("/games/{gameId}/color")
    @SendTo("/topic/games/{gameId}/color")
    public Map<String, String> getCurrentPlayerColor(@DestinationVariable Long gameId) {
        String color = gameService.getCurrentPlayerColor(gameId);
        return Map.of("color", color, "gameId", gameId.toString());
    }
}
