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

package com.tny.game.web.converter.excel;

import com.tny.game.common.reflect.*;
import com.tny.game.web.converter.excel.annotation.*;

import java.lang.reflect.InvocationTargetException;

public class ExcelFieldHolder implements Comparable<ExcelFieldHolder> {

    private ExcelColumn column;

    private PropertyAccessor accessor;

    public ExcelFieldHolder(ExcelColumn column, PropertyAccessor accessor) {
        super();
        this.column = column;
        this.accessor = accessor;
    }

    public ExcelColumn getColumn() {
        return column;
    }

    public int getIndex() {
        return column.index();
    }

    public Object get(Object object) {
        try {
            return accessor.getPropertyValue(object);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int compareTo(ExcelFieldHolder o) {
        return this.getIndex() - o.getIndex();
    }

    public String getColumnText() {
        return column.columnText();
    }

}
