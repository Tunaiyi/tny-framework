package com.tny.game.suite.core;

/**
 * 服务器类型
 * Created by Kun Yang on 16/1/26.
 */
public interface ScopeType {

    int getID();

    String getName();

    boolean isTest();

    <S extends AppType> S getAppType();

    default void registerSelf() {
        ScopeTypes.register(this);
    }

}
