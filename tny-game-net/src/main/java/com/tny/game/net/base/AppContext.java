package com.tny.game.net.base;

import com.tny.game.common.context.Attributes;

import java.util.List;

public interface AppContext {

    String getName();

    String getAppType();

    String getScopeType();

    Attributes attributes();

    String[] getScanPathArray();

    List<String> getScanPaths();

}
