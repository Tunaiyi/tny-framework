package com.tny.game.suite;

import com.tny.game.suite.core.ServerType;

/**
 * 服务器类型
 * Created by Kun Yang on 16/1/27.
 */
public enum TestServerType implements ServerType {


    GAME("game"),

    //
    ;


    String name;

    TestServerType(String name) {
        this.name = name;
        registerSelf();
    }

    @Override
    public String getName() {
        return name;
    }
}
