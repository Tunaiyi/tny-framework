package com.tny.game.suite;

import com.tny.game.net.base.ScopeType;
import com.tny.game.net.base.AppType;

/**
 * 服务器作用域类型
 * Created by Kun Yang on 16/1/27.
 */
public enum TestScopeType implements ScopeType {


    ONLINE(0, "online", TestAppType.GAME, false),

    //
    ;

    int id;
    String name;
    boolean test;
    TestAppType serverType;

    TestScopeType(int id, String name, TestAppType serverType, boolean test) {
        this.id = id;
        this.name = name;
        this.serverType = serverType;
        this.test = test;
        registerSelf();
    }


    @Override
    public int getID() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTest() {
        return false;
    }

    @Override
    public <S extends AppType> S getAppType() {
        return null;
    }
}
