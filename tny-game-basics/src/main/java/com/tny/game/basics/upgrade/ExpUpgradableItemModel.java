package com.tny.game.basics.upgrade;

/**
 * 通过经验升级的 item Model
 * Created by Kun Yang on 2017/4/5.
 */
public interface ExpUpgradableItemModel extends UpgradableItemModel {

    /**
     * @return 获取升级经验类型
     */
    ExpType getLevelExpType();

    /**
     * @param item 相关Item
     * @return 获取Item的最大升级经验
     */
    long getMaxLevelExp(ExpUpgradableItem<? extends ExpUpgradableItemModel> item, Object... attributes);

}
