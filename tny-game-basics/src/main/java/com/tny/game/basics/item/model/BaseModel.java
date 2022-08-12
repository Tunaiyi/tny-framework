/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.model;

import com.google.common.collect.ImmutableSet;
import com.tny.game.basics.item.*;

import java.util.Set;

/**
 * 抽象xml映射事物模型
 *
 * @author KGTny
 */
public abstract class BaseModel<C> implements Model {

    protected boolean init = false;

    protected Set<Object> tags;

    protected String currentFormula(String alias) {
        return null;
    }

    protected void init(C context) {
        if (init) {
            return;
        }
        if (this.tags == null) {
            this.tags = ImmutableSet.of();
        }
        this.doInit(context);
        init = true;
    }

    @Override
    public Set<Object> tags() {
        return tags;
    }

    protected abstract void doInit(C context);

}
