package com.tny.game.suite.base.module;

import com.tny.game.base.module.*;

public abstract class GameFeatureHandler<F extends Feature> implements FeatureHandler {

    protected F feature;

    public GameFeatureHandler(F feature) {
        super();
        this.feature = feature;
    }

    @Override
    public F getFeature() {
        return feature;
    }

    @Override
    public String toString() {
        return "FeatureHandler [feature=" + this.feature + "]";
    }

    // /**
    // * 更新游戏DTO
    // *
    // * @param playerInfo
    // * @return
    // */
    // public abstract BaseDTO updateGameDTO(ModuleOwner playerInfo, GameDTO dto);
    //
    // /**
    // * 获取模块信息DTO
    // *
    // * @param playerInfo
    // * @return
    // */
    // public abstract <DTO extends BaseDTO> DTO getModuleDTO(ModuleOwner playerInfo);

}
