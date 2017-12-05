package com.tny.game.suite.app;

import com.tny.game.common.context.Attributes;

public interface AppContext {

    String getName();

    String getAppType();

    String getScopeType();

    Attributes attributes();

}
