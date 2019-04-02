package com.tny.game.net.base;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.unit.annotation.*;

import java.util.List;

@UnitInterface
public interface AppContext {

    String getName();

    String getAppType();

    String getScopeType();

    Attributes attributes();

    String[] getScanPathArray();

    List<String> getScanPathList();

}
