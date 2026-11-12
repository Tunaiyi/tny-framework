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
