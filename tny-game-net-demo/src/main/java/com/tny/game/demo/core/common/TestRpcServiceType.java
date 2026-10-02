/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
