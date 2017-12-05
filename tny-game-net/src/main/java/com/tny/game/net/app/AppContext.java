package com.tny.game.net.app;

import com.tny.game.common.context.Attributes;

public interface AppContext {

    String getName();

    String getAppType();

    String getScopeType();

    Attributes attributes();

}
