package com.tny.game.common.utils.module;

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

    public abstract DTO updateDTO(FeatureExplorer explorer, DTO dto);

    @Override
    public void removeModule(FeatureExplorer explorer) {
    }

    @Override
    public String toString() {
        return "GameModule [getModuleType()=" + this.getModule() + "]";
    }

}
