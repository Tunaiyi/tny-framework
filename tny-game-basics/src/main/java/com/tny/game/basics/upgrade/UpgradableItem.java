package com.tny.game.basics.upgrade;

import com.tny.game.basics.item.*;

/**
 * 可升级Item对象
 * Created by Kun Yang on 2017/4/5.
 */
public interface UpgradableItem<IM extends UpgradableItemModel> extends Item<IM> {

    /**
     * @return 获得升级
     */
    int getLevel();

    /**
     * @return 是否是最高升级
     */
    default boolean isMaxLevel() {
        int maxLevel = this.getMaxLevel();
        if (maxLevel < 0) {
            return false;
        }
        return this.getLevel() >= maxLevel;
    }

    /**
     * @return 获取最高升级
     */
    default int getMaxLevel() {
        return this.getModel().getMaxLevel(this);
    }

    /**
     * @return 是否有最高升级
     */
    default boolean hasMaxLevel() {
        return this.getModel().hasMaxLevel();
    }

    /**
     * @return 升级初始等级
     */
    default int getInitLevel() {
        return this.getModel().getInitLevel();
    }

}
