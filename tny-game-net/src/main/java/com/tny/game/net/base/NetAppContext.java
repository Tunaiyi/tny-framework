package com.tny.game.net.base;

import com.tny.game.common.context.*;
import com.tny.game.common.unit.annotation.*;

import java.util.List;

@UnitInterface
public interface NetAppContext {

    String getName();

    String getAppType();

    String getScopeType();

    Attributes attributes();

    List<String> getScanPackages();

}
