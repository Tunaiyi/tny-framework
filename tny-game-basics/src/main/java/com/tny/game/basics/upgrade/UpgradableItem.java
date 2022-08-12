/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
