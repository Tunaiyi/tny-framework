package com.tny.game.net.base;

import com.tny.game.common.context.Attributes;

public interface AppContext {

    String getName();

    String getAppType();

    String getScopeType();

    Attributes attributes();

}
