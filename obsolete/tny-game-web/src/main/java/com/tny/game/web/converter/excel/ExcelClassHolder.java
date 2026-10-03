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
