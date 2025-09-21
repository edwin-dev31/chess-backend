package com.chess.game.config.socket;

import com.chess.game.application.dto.player.PlayerOnlineDTO;
import com.chess.game.application.service.interfaces.IPlayerService;
import com.chess.game.infrastructure.entity.PlayerEntity;
import com.chess.game.util.enums.PlayerStatus;
import com.chess.game.util.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PresenceEventListener {

    private final Set<PlayerOnlineDTO> onlinePlayers = ConcurrentHashMap.newKeySet();
    private final SimpMessagingTemplate messagingTemplate;
    private final IPlayerService playerService;

    public PresenceEventListener(SimpMessagingTemplate messagingTemplate,
                                 IPlayerService playerService) {
        this.messagingTemplate = messagingTemplate;
        this.playerService = playerService;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        Principal user = event.getUser();
        
        if (user != null) {
            PlayerEntity player = playerService.findById(Long.parseLong(user.getName())).orElseThrow(
                    () -> new ResourceNotFoundException("Player not found"));

            log.warn(player.toString());
            PlayerOnlineDTO dto = PlayerOnlineDTO.builder()
                    .id(player.getId())
                    .username(player.getUsername())
                    .imageUrl(player.getImageUrl())
                    .status(PlayerStatus.ONLINE)
                    .build();
            log.warn(dto.toString());
            onlinePlayers.add(dto);

           // playerService.updateStatusByUsername(player.getId(), PlayerStatus.ONLINE);

            broadcastOnlinePlayers();
        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            onlinePlayers.removeIf(p -> p.getUsername().equals(user.getName()));

            // playerService.updateStatusByUsername(user.getName(), PlayerStatus.OFFLINE);

            broadcastOnlinePlayers();
        }
    }

    private void broadcastOnlinePlayers() {
        messagingTemplate.convertAndSend("/topic/online-players", onlinePlayers);
    }

    public Set<PlayerOnlineDTO> getOnlinePlayers() {
        return onlinePlayers;
    }
}
