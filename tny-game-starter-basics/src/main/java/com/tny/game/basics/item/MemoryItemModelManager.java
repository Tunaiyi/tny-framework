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

package com.tny.game.basics.item;

import javax.annotation.PostConstruct;
import java.util.List;

public abstract class MemoryItemModelManager<IM extends ItemModel> extends GameItemModelManager<IM> {

    protected MemoryItemModelManager(Class<? extends IM> modelClass) {
        super(modelClass);
    }

    @Override
    @PostConstruct
    protected void initManager() {
        try {
            this.loadAndInitModel(loadAllModels(), ".", false);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        synchronized (this) {
            this.parseAllComplete();
        }
    }

    protected abstract List<IM> loadAllModels();

}
