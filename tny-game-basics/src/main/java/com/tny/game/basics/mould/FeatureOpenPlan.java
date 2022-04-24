package com.tny.game.basics.mould;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2018/4/29.
 */
public class FeatureOpenPlan {

    private int level;

    private FeatureOpenMode<?> mode;

    public int getLevel() {
        return this.level;
    }

    public <FM extends FeatureModel> FeatureOpenMode<FM> getMode() {
        return as(this.mode);
    }

}
