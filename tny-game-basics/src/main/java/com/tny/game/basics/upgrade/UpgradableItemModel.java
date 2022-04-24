package com.tny.game.basics.upgrade;

import com.tny.game.basics.item.*;

/**
 * 可升级对象Model
 * Created by Kun Yang on 2017/4/5.
 */
public interface UpgradableItemModel extends ItemModel {

    /**
     * @return 是否有最高升级
     */
    boolean hasMaxLevel();

    /**
     * @param item 相关Item
     * @return 获取Item的最大升级
     */
    int getMaxLevel(UpgradableItem<? extends UpgradableItemModel> item, Object... attributes);

    /**
     * @return 获取升级初始等级
     */
    int getInitLevel();

}
