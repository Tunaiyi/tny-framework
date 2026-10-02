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

package com.tny.game.basics.item;

import com.tny.game.expr.*;

/**
 * Created by Kun Yang on 2018/6/4.
 */
public class DefaultItemModelContext implements ItemModelContext {

    private final ItemExplorer itemExplorer;

    private final ModelExplorer itemModelExplorer;

    private final ExprHolderFactory exprHolderFactory;

    public DefaultItemModelContext(ItemExplorer itemExplorer, ModelExplorer itemModelExplorer, ExprHolderFactory exprHolderFactory) {
        this.itemExplorer = itemExplorer;
        this.itemModelExplorer = itemModelExplorer;
        this.exprHolderFactory = exprHolderFactory;
    }

    @Override
    public ItemExplorer getItemExplorer() {
        return itemExplorer;
    }

    @Override
    public ModelExplorer getItemModelExplorer() {
        return itemModelExplorer;
    }

    @Override
    public ExprHolderFactory getExprHolderFactory() {
        return exprHolderFactory;
    }

}
