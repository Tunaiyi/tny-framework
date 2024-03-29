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

package com.tny.game.web.converter.excel;

import com.tny.game.common.reflect.*;
import com.tny.game.common.reflect.javassist.*;
import com.tny.game.web.converter.excel.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

public class ExcelClassHolder {

    private ClassAccessor gClass;

    private ExcelSheet sheet;

    private Set<ExcelFieldHolder> fieldHolders = new TreeSet<>();

    private ExcelClassHolder() {
    }

    public String getSheetName() {
        return sheet.value();
    }

    public static ExcelClassHolder create(Class<?> clazz) {
        ExcelSheet sheet = clazz.getAnnotation(ExcelSheet.class);
        if (sheet == null) {
            return null;
        }
        ExcelClassHolder holder = new ExcelClassHolder();
        holder.sheet = sheet;
        holder.gClass = JavassistAccessors.getGClass(clazz);
        for (Field field : ReflectAide.getDeepField(clazz)) {
            ExcelColumn column = field.getAnnotation(ExcelColumn.class);
            if (column == null) {
                continue;
            }
            String name = column.name();
            if (StringUtils.isBlank(name)) {
                name = field.getName();
            }
            PropertyAccessor accessor = holder.gClass.getProperty(name);
            if (accessor == null) {
                throw new NullPointerException(format("{} 不存在 {} property", clazz, name));
            }
            if (!holder.fieldHolders.add(new ExcelFieldHolder(column, accessor))) {
                throw new IllegalArgumentException(format("{} 属性 {} 字段索引 {} 有冲突", clazz, name, column.index()));
            }
        }
        return holder;
    }

    protected ClassAccessor getgClass() {
        return gClass;
    }

    protected ExcelSheet getSheet() {
        return sheet;
    }

    protected Collection<ExcelFieldHolder> getFieldHolders() {
        return Collections.unmodifiableCollection(fieldHolders);
    }

}
