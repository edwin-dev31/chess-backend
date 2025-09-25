package com.chess.game.application.dto.game;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private String content;
    private String md5;
}