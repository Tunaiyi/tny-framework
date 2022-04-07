package com.tny.game.doc.enums;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.doc.table.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class AllEnumsTableAttribute implements TableAttribute {

	private EnumList enumList = new EnumList();

	private Class<? extends EnumConfiger> enumConfigClass;

	@XStreamAlias("enumerList")
	private static class EnumList {

		@XStreamAsAttribute
		@XStreamAlias("class")
		private String type = "list";

		@XStreamImplicit(itemFieldName = "enum")
		private List<EnumConfiger> enumerList = new ArrayList<>();

	}

	public AllEnumsTableAttribute(Class<? extends EnumConfiger> enumConfigClass) {
		this.enumConfigClass = enumConfigClass;
	}

	@SuppressWarnings("rawtypes")
	public AllEnumsTableAttribute(Class<Enum> clazz, Class<? extends EnumConfiger> enumConfigClass, TypeFormatter typeFormatter) {
		super();
		try {
			this.enumConfigClass = enumConfigClass;
			EnumConfiger configer = this.enumConfigClass.newInstance();
			configer.setEnumDocHolder(EnumDocHolder.create(clazz), typeFormatter);
			this.enumList.enumerList.add(configer);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes) {
		try {
			EnumConfiger configer = this.enumConfigClass.newInstance();
			configer.setEnumDocHolder(EnumDocHolder.create((Class<Enum>)clazz), typeFormatter);
			this.enumList.enumerList.add(configer);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> getContext() {
		return MapBuilder.<String, Object>newBuilder()
				.put("enumerList", enumList)
				.build();
	}

	public EnumList getEnumeration() {
		return this.enumList;
	}

}
