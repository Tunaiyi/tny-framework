package com.tny.game.basics.upgrade;

import com.tny.game.basics.item.*;

/**
 * 升级器
 * Created by Kun Yang on 2017/4/5.
 */
public interface ExpUpdater<I extends Item<?>> extends Updater<I> {

    @Override
    default boolean isPromoted() {
        return this.getExp() > 0 || Updater.super.isPromoted();
    }

    default long getUpgradeModelId() {
        return item().getId();
    }

    default int getUpgradeItemModelId() {
        return item().getModelId();
    }

    /**
     * @return 玩家Id
     */
    default long getPlayerId() {
        return this.item().getPlayerId();
    }

    /**
     * @return 经验
     */
    long getExp();

    /**
     * @return 获取最大经验
     */
    long getMaxExp();

    /**
     * @return 是否满经验
     */
    default boolean isExpFully() {
        return this.getExp() >= this.getMaxExp();
    }

    /**
     * @return 经验进度
     */
    default float getRateOfProgress() {
        return (float)((double)this.getExp() / this.getMaxExp());
    }

    default boolean isUpgradable() {
        return !isMaxLevel() && this.isExpFully();
    }

    /**
     * @return 经验类型
     */
    ExpType getExpType();

}
