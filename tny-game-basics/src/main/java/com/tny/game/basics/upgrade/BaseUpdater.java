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
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.upgrade.notify.*;

/**
 * 抽象升级器
 * Created by Kun Yang on 2018/3/23.
 */
public abstract class BaseUpdater<I extends Item<?>> implements Updater<I> {

    protected I item;

    protected int level;

    protected OnPreUpgrade<I> onPreUpgrade;

    protected OnUpgrade<I> onUpgrade;

    protected OnReset<I> onReset;

    protected BaseUpdater() {
    }

    protected BaseUpdater(I item, int level) {
        this.item = item;
        this.level = level;
    }

    protected void bind(I item) {
        this.setItem(item);
    }

    @Override
    public I item() {
        return item;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public int upgrade(Action action, int upgradeLevel) {
        int maxLevel = getMaxLevel();
        int level = this.level;
        if (level >= maxLevel) {
            return 0;
        }
        int oldLevel = level;
        level = Math.min(maxLevel, oldLevel + upgradeLevel);
        upgradeLevel = level - oldLevel;
        if (onPreUpgrade != null) {
            onPreUpgrade.preUpgrade(this.item, action, level);
        }
        this.level = level;
        if (onUpgrade != null) {
            onUpgrade.upgrade(this.item, action, oldLevel, upgradeLevel);
        }
        return upgradeLevel;
    }

    public void reset(Action action) {
        int oldLevel = this.level;
        this.level = this.getInitLevel();
        if (onReset != null) {
            onReset.reset(item, action, oldLevel);
        }
    }

    protected BaseUpdater<I> setItem(I item) {
        this.item = item;
        return this;
    }

    protected BaseUpdater<I> setLevel(int level) {
        this.level = level;
        return this;
    }

    protected BaseUpdater<I> withOnPreUpgrade(OnPreUpgrade<I> onPreUpgrade) {
        this.onPreUpgrade = onPreUpgrade;
        return this;
    }

    protected BaseUpdater<I> withOnUpgrade(OnUpgrade<I> onUpgrade) {
        this.onUpgrade = onUpgrade;
        return this;
    }

    protected BaseUpdater<I> withOnReset(OnReset<I> onReset) {
        this.onReset = onReset;
        return this;
    }

}
