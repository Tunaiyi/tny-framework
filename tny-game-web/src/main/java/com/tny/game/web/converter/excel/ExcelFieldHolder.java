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
