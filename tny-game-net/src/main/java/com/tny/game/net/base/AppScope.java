package com.tny.game.net.base;

import com.tny.game.common.enums.*;

/**
 * 服务器类型
 * Created by Kun Yang on 16/1/26.
 */
public interface AppScope extends IntEnumerable {

    String getScopeName();
    
    default void registerSelf() {
        AppScopes.register(this);
    }

}
