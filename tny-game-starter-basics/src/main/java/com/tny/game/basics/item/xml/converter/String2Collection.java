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
