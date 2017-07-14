package com.tny.game.base.module.listener;

import com.tny.game.base.module.FeatureExplorer;
import com.tny.game.base.module.Module;
import com.tny.game.common.event.BindP1EventBus;
import com.tny.game.common.event.EventBuses;

public interface ModuleListener {

    BindP1EventBus<ModuleListener, FeatureExplorer, Module> OPEN_MODULE_EVENT = EventBuses.of(ModuleListener.class,
            ModuleListener::handleOpenModule);

    void handleOpenModule(FeatureExplorer explorer, Module openedModule);

}