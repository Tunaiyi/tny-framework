/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
