package com.chess.game.config.socket;

import java.security.Principal;

public class UserPrincipal implements Principal {
    private final String id;

    public UserPrincipal(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return id;
    }
}