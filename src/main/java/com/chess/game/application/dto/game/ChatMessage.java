package com.chess.game.application.dto.game;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessage{
    private Long from;
    private String fromUsername;
    private Long to;
    private String toUsername;
    private String content;
    private LocalDateTime createAt;
}