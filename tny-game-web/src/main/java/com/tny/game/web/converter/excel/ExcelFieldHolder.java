package com.tny.game.web.converter.excel;


import com.tny.game.common.reflect.GPropertyAccessor;
import com.tny.game.web.converter.excel.annotation.ExcelColumn;

import java.lang.reflect.InvocationTargetException;

public class ExcelFieldHolder implements Comparable<ExcelFieldHolder> {

	private ExcelColumn column;

	private GPropertyAccessor accessor;

	public ExcelFieldHolder(ExcelColumn column, GPropertyAccessor accessor) {
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
			return accessor.getPorpertyValue(object);
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
