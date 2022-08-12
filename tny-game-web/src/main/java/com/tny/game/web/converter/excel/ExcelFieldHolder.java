/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
