package com.tny.game.suite.app;

/**
 * 服务器类型
 * Created by Kun Yang on 16/1/26.
 */
public interface AppType {

    String getName();

    default void registerSelf() {
        AppTypes.register(this);
    }

}
