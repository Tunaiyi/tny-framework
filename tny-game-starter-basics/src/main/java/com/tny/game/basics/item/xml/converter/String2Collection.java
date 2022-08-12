/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.xml.converter;

import com.tny.game.basics.converter.*;
import com.tny.game.expr.*;

import java.util.Collection;

@SuppressWarnings({"rawtypes"})
public class String2Collection extends String2ExprHolderConverter {

    private final Class<? extends Collection> clazz;

    public String2Collection(ExprHolderFactory exprHolderFactory, Class<? extends Collection> clazz) {
        super(exprHolderFactory);
        this.clazz = clazz;
    }

    @Override
    public boolean canConvert(Class type) {
        return this.clazz.isAssignableFrom(type);
    }

    @Override
    public Object fromString(String str) {
        return this.exprHolderFactory.create(str).createExpr().execute(this.clazz);
    }

}
