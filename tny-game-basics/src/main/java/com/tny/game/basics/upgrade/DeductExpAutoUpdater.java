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
