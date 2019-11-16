package com.tny.game.suite.base.module;

import com.tny.game.base.module.*;

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
