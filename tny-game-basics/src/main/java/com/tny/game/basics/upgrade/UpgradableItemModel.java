/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
