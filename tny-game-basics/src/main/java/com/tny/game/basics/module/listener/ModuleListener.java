package com.tny.game.basics.module.listener;

import com.tny.game.basics.module.*;
import com.tny.game.common.event.bus.*;

public interface ModuleListener {

    BindP1EventBus<ModuleListener, FeatureExplorer, Module> OPEN_MODULE_EVENT = EventBuses.of(ModuleListener.class,
            ModuleListener::handleOpenModule);

    void handleOpenModule(FeatureExplorer explorer, Module openedModule);

}