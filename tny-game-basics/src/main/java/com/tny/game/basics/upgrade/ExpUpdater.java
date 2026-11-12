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
        return (float) ((double) this.getExp() / this.getMaxExp());
    }

    default boolean isUpgradable() {
        return !isMaxLevel() && this.isExpFully();
    }

    /**
     * @return 经验类型
     */
    ExpType getExpType();

}
