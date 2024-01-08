package com.tny.game.demo.core.common;

import com.tny.game.net.application.*;

public enum TestRpcServiceType implements RpcServiceType {

    GAME_RPC(100, TestAppType.GAME, "game-service"),
    GAME(101, TestAppType.GAME, "game-server"),
    GATEWAY_RPC(102, TestAppType.GAME, "gateway-service"),
    GAME_CLIENT(200, TestAppType.GAME_CLIENT, "game-client"),

    //
    ;

    private final int id;

    private final String service;

    private final AppType appType;

    TestRpcServiceType(int id, AppType appType, String service) {
        this.id = id;
        this.service = service;
        this.appType = appType;
        this.register();
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String getService() {
        return service;
    }

    // @Override
    // public AppType getAppType() {
    //     return appType;
    // }

}
