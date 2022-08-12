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

/**
 * 升级器
 * Created by Kun Yang on 2017/4/5.
 */
public interface Updater<I> {

    /**
     * @return 获取可升级Item
     */
    I item();

    /**
     * @return 等级
     */
    int getLevel();

    /**
     * @return 获取最高等级
     */
    int getMaxLevel();

    /**
     * @return 获取初始等级
     */
    default int getInitLevel() {
        return 0;
    }

    /**
     * @return 是否是最高等级
     */
    default boolean isMaxLevel() {
        return this.hasMaxLevel() && this.getLevel() >= this.getMaxLevel();
    }

    /**
     * @return 是否最高等级
     */
    default boolean hasMaxLevel() {
        return this.getMaxLevel() > 0;
    }

    default boolean isPromoted() {
        return this.getLevel() > getInitLevel();
    }

}
