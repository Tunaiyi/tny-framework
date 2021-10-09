package com.tny.game.basics.item.module;

import com.tny.game.basics.module.*;

public class DefaultModuleHandler extends GameModuleHandler<Module, Object> {

    public DefaultModuleHandler(Module moduleType) {
        super(moduleType);
    }

    @Override
    public boolean openModule(FeatureExplorer explorer) {
        return true;
    }

    @Override
    public void loadModule(FeatureExplorer explorer) {

    }

    @Override
    public Object updateDTO(FeatureExplorer featureExplorer, Object o) {
        return null;
    }

}
