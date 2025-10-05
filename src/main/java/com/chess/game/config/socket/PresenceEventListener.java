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
    public void handleSessionConnected(SessionConnectEvent event) throws InterruptedException {
        Principal user = event.getUser();
        
        if (user != null) {
            PlayerEntity player = playerService.findById(Long.parseLong(user.getName())).orElseThrow(
                    () -> new ResourceNotFoundException("Player not found"));

            PlayerOnlineDTO dto = PlayerOnlineDTO.builder()
                    .id(player.getId())
                    .username(player.getUsername())
                    .imageUrl(player.getImageUrl())
                    .status(PlayerStatus.ONLINE)
                    .build();
            onlinePlayers.add(dto);

            playerService.updateStatus(player.getId(), PlayerStatus.ONLINE);

            new Thread(() -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
                broadcastOnlinePlayers();
                messagingTemplate.convertAndSendToUser(
                        player.getId().toString(),
                        "/queue/online-players",
                        onlinePlayers
                );
            }).start();
        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            PlayerEntity player = playerService.findById(Long.parseLong(user.getName())).orElseThrow(
                    () -> new ResourceNotFoundException("Player not found"));

            removeOnlinePlayer(user.getName());
            playerService.updateStatus(player.getId(), PlayerStatus.OFFLINE);

        }
    }

    private void broadcastOnlinePlayers() {
        messagingTemplate.convertAndSend("/topic/online-players", onlinePlayers);
    }

    public void removeOnlinePlayer(String playerId) {
        onlinePlayers.removeIf(p -> String.valueOf(p.getId()).equals(playerId));
        broadcastOnlinePlayers();
    }

    public Set<PlayerOnlineDTO> getOnlinePlayers() {
        return onlinePlayers;
    }
}
