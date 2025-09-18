package com.chess.game.presentation.rest;

import com.chess.game.application.dto.game.InvitationDto;
import com.chess.game.application.service.interfaces.IGameService;
import com.chess.game.config.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/games")
public class InvitateGameController {

    private final IGameService gameService;
    private final JwtUtil jwt;
    private final SimpMessagingTemplate messagingTemplate;

    public InvitateGameController(IGameService gameService,
                                  JwtUtil jwt,
                                  SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.jwt = jwt;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/{toUserId}/invite")
    public ResponseEntity<Map<String, String>> invitePlayer(@PathVariable Long toUserId,
                                               @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long fromPlayerId = jwt.extractId(token);

        InvitationDto invitation = gameService.createInvitation(fromPlayerId, toUserId);

        messagingTemplate.convertAndSendToUser(
                toUserId.toString(),
                "/queue/invitations",
                invitation
        );
        log.warn(String.valueOf(invitation));
        return ResponseEntity.ok(Map.of("message", invitation + ""));
    }

    @PostMapping("/{fromPlayerId}/accept")
    public ResponseEntity<Map<String, String>> acceptInvitation(@PathVariable Long fromPlayerId,
                                                          @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long toPlayerId = jwt.extractId(token);

        InvitationDto invitation = gameService.acceptInvitation(fromPlayerId, toPlayerId);

        messagingTemplate.convertAndSendToUser(
                invitation.getFromUserId().toString(),
                "/queue/invitations",
                invitation
        );
        messagingTemplate.convertAndSendToUser(
                invitation.getToUserId().toString(),
                "/queue/invitations",
                invitation
        );

        return ResponseEntity.ok(Map.of("message", "Invitation accepted"));
    }

    @PostMapping("/{toPlayerId}/reject")
    public ResponseEntity<Map<String, String>> rejectInvitation(@PathVariable Long toPlayerId,
                                                          @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long playerId = jwt.extractId(token);

        InvitationDto invitation = gameService.rejectInvitation(toPlayerId, playerId);

        messagingTemplate.convertAndSendToUser(
                invitation.getFromUserId().toString(),
                "/queue/invitations",
                invitation
        );

        return ResponseEntity.ok(Map.of("message", "Invitation rejected"));
    }
}
