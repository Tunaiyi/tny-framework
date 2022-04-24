package com.tny.game.suite;

import com.tny.game.net.base.*;

/**
 * 服务器类型
 * Created by Kun Yang on 16/1/27.
 */
public enum TestAppType implements AppType {

    GAME("game"),

    //
    ;

    String name;

    TestAppType(String name) {
        this.name = name;
        registerSelf();
    }

    @Override
    public String getName() {
        return name;
    }
}
