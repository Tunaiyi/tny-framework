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

import com.tny.game.web.converter.excel.annotation.*;
import org.apache.poi.ss.usermodel.*;

import java.util.*;
import java.util.concurrent.*;

public class Object2ExcelFormatter {

    private static final ConcurrentMap<Class<?>, ExcelClassHolder> fieldHolders = new ConcurrentHashMap<>();

    public static void fill(Workbook book, Collection<?> objects) {
        int rowIndex = 1;
        for (Object object : objects) {
            ExcelClassHolder holder = getHolder(object.getClass());
            if (holder == null) {
                continue;
            }
            Sheet sheet = book.getSheet(holder.getSheetName());
            if (sheet == null) {
                sheet = book.createSheet(holder.getSheetName());
                int index = 0;
                Row titleRow = sheet.createRow(0);
                for (ExcelFieldHolder fieldHolder : holder.getFieldHolders()) {
                    ExcelColumn column = fieldHolder.getColumn();
                    sheet.setColumnWidth(index, column.width());
                    Object value = fieldHolder.getColumnText();
                    setCell(titleRow.createCell(index), value);
                    index++;
                }
            }
            Row valueRow = sheet.createRow(rowIndex++);
            int index = 0;
            for (ExcelFieldHolder fieldHolder : holder.getFieldHolders()) {
                Object value = fieldHolder.get(object);
                setCell(valueRow.createCell(index++), value);
            }
        }
    }

    private static void setCell(Cell cell, Object value) {
        if (value == null) {
            return;
        }
        CellStyle style = cell.getCellStyle();
        style.setWrapText(true);
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static ExcelClassHolder getHolder(Class<?> clazz) {
        ExcelClassHolder classHolder = fieldHolders.get(clazz);
        if (classHolder != null) {
            return classHolder;
        }
        classHolder = ExcelClassHolder.create(clazz);
        if (classHolder == null) {
            return null;
        }
        ExcelClassHolder old = fieldHolders.putIfAbsent(clazz, classHolder);
        return old == null ? classHolder : old;
    }

}
