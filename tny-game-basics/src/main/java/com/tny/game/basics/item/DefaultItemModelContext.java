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
