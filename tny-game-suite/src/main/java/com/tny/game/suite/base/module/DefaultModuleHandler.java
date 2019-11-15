package com.tny.game.suite.base.module;

import com.tny.game.base.module.*;

public class DefaultModuleHandler extends GameModuleHandler<Module, Object> {

    public DefaultModuleHandler(Module moduleType) {
        super(moduleType);
    }

    @Override
    public boolean openModule(FeatureExplorer owner) {
        return true;
    }

    @Override
    public void loadModule(FeatureExplorer owner) {

    }

    @Override
    public Object updateDTO(FeatureExplorer featureExplorer, Object o) {
        return null;
    }

}
