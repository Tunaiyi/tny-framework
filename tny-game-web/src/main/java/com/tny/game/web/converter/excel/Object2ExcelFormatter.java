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
            cell.setCellValue(((Number)value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean)value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar)value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date)value);
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
