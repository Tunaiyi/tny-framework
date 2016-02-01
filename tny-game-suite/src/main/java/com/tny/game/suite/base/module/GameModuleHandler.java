package com.tny.game.suite.base.module;

import com.tny.game.base.module.FeatureExplorer;
import com.tny.game.base.module.Module;
import com.tny.game.base.module.ModuleHandler;

public abstract class GameModuleHandler<M extends Module, DTO> implements ModuleHandler {

    protected M moduleType;

    public GameModuleHandler(M moduleType) {
        super();
        this.moduleType = moduleType;
    }

    @Override
    public Module getModule() {
        return this.moduleType;
    }

    public abstract DTO updateDTO(FeatureExplorer featureExplorer, DTO dto);

    @Override
    public void removeModule(FeatureExplorer featureExplorer) {
    }

    @Override
    public String toString() {
        return "GameModule [getModuleType()=" + this.getModule() + "]";
    }

}
