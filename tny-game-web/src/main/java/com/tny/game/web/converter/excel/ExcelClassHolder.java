package com.tny.game.web.converter.excel;


import com.tny.game.LogUtils;
import com.tny.game.common.reflect.GClass;
import com.tny.game.common.reflect.GPropertyAccessor;
import com.tny.game.common.reflect.ReflectUtils;
import com.tny.game.common.reflect.javassist.JSsistUtils;
import com.tny.game.web.converter.excel.annotation.ExcelColumn;
import com.tny.game.web.converter.excel.annotation.ExcelSheet;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class ExcelClassHolder {

	private GClass gClass;

	private ExcelSheet sheet;

	private Set<ExcelFieldHolder> fieldHolders = new TreeSet<>();

	private ExcelClassHolder() {
	}

	public String getSheetName() {
		return sheet.value();
	}

	public static ExcelClassHolder create(Class<?> clazz) {
		ExcelSheet sheet = clazz.getAnnotation(ExcelSheet.class);
		if (sheet == null)
			return null;
		ExcelClassHolder holder = new ExcelClassHolder();
		holder.sheet = sheet;
		holder.gClass = JSsistUtils.getGClass(clazz);
		for (Field field : ReflectUtils.getDeepField(clazz)) {
			ExcelColumn column = field.getAnnotation(ExcelColumn.class);
			if (column == null)
				continue;
			String name = column.name();
			if (StringUtils.isBlank(name))
				name = field.getName();
			GPropertyAccessor accessor = holder.gClass.getProperty(name);
			if (accessor == null)
				throw new NullPointerException(LogUtils.format("{} 不存在 {} property", clazz, name));
			if (!holder.fieldHolders.add(new ExcelFieldHolder(column, accessor)))
				throw new IllegalArgumentException(LogUtils.format("{} 属性 {} 字段索引 {} 有冲突", clazz, name, column.index()));
		}
		return holder;
	}

	protected GClass getgClass() {
		return gClass;
	}

	protected ExcelSheet getSheet() {
		return sheet;
	}

	protected Collection<ExcelFieldHolder> getFieldHolders() {
		return Collections.unmodifiableCollection(fieldHolders);
	}

}
