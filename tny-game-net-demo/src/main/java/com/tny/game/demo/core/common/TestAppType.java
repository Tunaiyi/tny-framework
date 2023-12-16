package com.tny.game.demo.core.common;

import com.tny.game.net.base.*;

public enum TestAppType implements AppType {

    GAME(100, "game-service"),
    GAME_CLIENT(200, "game-client"),
    ;

    private final int id;

    private final String name;

    TestAppType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String getAppName() {
        return name;
    }
}
