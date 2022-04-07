package com.tny.game.doc.enums;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.doc.table.*;

import java.util.Map;

import static com.tny.game.doc.holder.EnumDocHolder.*;

public class EnumTableAttribute implements TableAttribute {

	private EnumConfiger enumeration;

	@XStreamOmitField
	private ExportHolder exportHolder;

	public EnumTableAttribute() {
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public EnumTableAttribute(Class<Enum> clazz, TypeFormatter typeFormatter) {
		this.enumeration = new EnumConfiger();
		this.enumeration.setEnumDocHolder(create(clazz), typeFormatter);
		this.exportHolder = ExportHolder.create(clazz);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes) {
		this.enumeration = new EnumConfiger();
		this.enumeration.setEnumDocHolder(create((Class<Enum>)clazz), typeFormatter);
		this.exportHolder = ExportHolder.create(clazz);
	}

	public EnumConfiger getEnumeration() {
		return this.enumeration;
	}

	@Override
	public String getOutput() {
		return this.exportHolder.getOutput();
	}

	@Override
	public Map<String, Object> getContext() {
		return MapBuilder.<String, Object>newBuilder()
				.put("enumeration", enumeration)
				.build();
	}

	@Override
	public String getTemplate() {
		return this.exportHolder.getTemplate();
	}

}
