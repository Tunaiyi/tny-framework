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
import com.tny.game.basics.item.behavior.*;

/**
 * 满经验自动扣除经验并升级
 * Created by Kun Yang on 2017/4/5.
 */
public abstract class DeductExpAutoUpdater<I extends Item<?>, EM extends ExpModel> extends BaseExpUpdater<I, EM> {

    protected DeductExpAutoUpdater(ExpType expType) {
        super(expType);
        this.setUpgradeOnMax(true);
    }

    protected DeductExpAutoUpdater(I item, int level, ExpType expType, long exp) {
        super(item, level, expType, exp, true);
    }

    @Override
    protected int doUpgrade(Action action) {
        long maxExp = getMaxExp();
        int oldLevel = this.level;
        int alter = 0;
        int maxLevel = this.getMaxLevel();
        while ((maxLevel < 0 || this.level < maxLevel) && this.exp >= maxExp) {
            if (this.level == oldLevel) {
                prepareUpgrade(action, 1);
            }
            this.level++;
            alter++;
            this.exp -= maxExp;
            maxExp = getMaxExp();
        }
        if (alter > 0) {
            postUpgrade(action, oldLevel, alter);
            return alter;
        }
        return 0;
    }

}
