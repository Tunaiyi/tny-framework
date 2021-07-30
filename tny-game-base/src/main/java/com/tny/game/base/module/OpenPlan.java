package com.tny.game.base.module;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2018/4/29.
 */
public class OpenPlan {

    private int level;

    private OpenMode<?> mode;

    public int getLevel() {
        return this.level;
    }

    public <FM extends FeatureModel> OpenMode<FM> getMode() {
        return as(this.mode);
    }

}
