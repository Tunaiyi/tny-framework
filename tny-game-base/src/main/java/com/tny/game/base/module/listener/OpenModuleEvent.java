package com.tny.game.base.module.listener;

import com.tny.game.base.module.FeatureExplorer;
import com.tny.game.base.module.Module;
import com.tny.game.event.Event;

public class OpenModuleEvent extends Event<FeatureExplorer> {

    private Module moduleType;

    public static void dispatch(FeatureExplorer source, Module moduleType) {
        new OpenModuleEvent(source, moduleType).dispatch();
    }

    private OpenModuleEvent(FeatureExplorer source, Module moduleType) {
        super("openModule", source);
        this.moduleType = moduleType;
    }

    public Module getOpenModuleType() {
        return this.moduleType;
    }

}
