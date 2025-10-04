package com.chess.game.presentation.socket;

import com.chess.game.application.dto.game.ChatMessage;
import com.chess.game.application.dto.game.ChatMessageRequest;
import com.chess.game.application.service.interfaces.IGameService;
import com.chess.game.application.service.interfaces.IPlayerService;
import com.chess.game.config.jwt.JwtUtil;
import com.chess.game.domain.HashidsUtil;
import com.chess.game.util.exception.IllegalStateExceptionCustom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
public class GameSocketController {

    private final IGameService gameService;
    private final IPlayerService playerService;
    private final JwtUtil jwt;
    private final SimpMessagingTemplate messagingTemplate;

    public GameSocketController(IGameService gameService, IPlayerService playerService, JwtUtil jwt, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.jwt = jwt;
        this.messagingTemplate = messagingTemplate;
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

    @MessageMapping("/{gameId}/chat")
    public void sendPrivate(@DestinationVariable String gameId,
                            ChatMessageRequest message,
                            Principal principal) {

        String hash = DigestUtils.md5Hex(message.getContent());
        if (!hash.equals(message.getMd5())) {
            throw new IllegalStateExceptionCustom("The content was corrupted");
        }

        Long playerId = Long.parseLong(principal.getName());
        Long opponentId = gameService.getOpponentId(HashidsUtil.decodeId(gameId), playerId);

        ChatMessage chatMessage = ChatMessage.builder()
                .from(playerId)
                .fromUsername(playerService.getProfile(playerId).getUsername())
                .to(opponentId)
                .toUsername(playerService.getProfile(opponentId).getUsername())
                .content(message.getContent())
                .build();

        messagingTemplate.convertAndSendToUser(
                opponentId.toString(),
                "/queue/messages",
                chatMessage
        );
    }

}
