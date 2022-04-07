package com.tny.game.doc.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.xstream.annotations.*;
import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.doc.table.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class AllEnumItemsTableAttribute implements TableAttribute {

	private EnumerList enumerList = new EnumerList();

	@JsonIgnore
	private Class<? extends EnumConfiger> enumConfigClass;

	@XStreamAlias("enumerList")
	private static class EnumerList {

		@XStreamAsAttribute
		@XStreamAlias("class")
		private String type = "list";

		@XStreamImplicit(itemFieldName = "enumer")
		private List<EnumerConfiger> enumerList = new ArrayList<EnumerConfiger>();

	}

	public AllEnumItemsTableAttribute(Class<? extends EnumConfiger> enumConfigClass, TypeFormatter typeFormatter) {
		this.enumConfigClass = enumConfigClass;
	}

	@SuppressWarnings("rawtypes")
	public AllEnumItemsTableAttribute(Class<Enum> clazz, Class<? extends EnumConfiger> enumConfigClass, TypeFormatter typeFormatter) {
		super();
		try {
			this.enumConfigClass = enumConfigClass;
			EnumConfiger configer = this.enumConfigClass.newInstance();
			configer.setEnumDocHolder(EnumDocHolder.create(clazz), typeFormatter);
			this.enumerList.enumerList.addAll(configer.getEnumerList());
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
			this.enumerList.enumerList.addAll(configer.getEnumerList());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> getContext() {
		return MapBuilder.<String, Object>newBuilder()
				.put("enumerList", enumerList)
				.build();
	}

	public EnumerList getEnumeration() {
		return this.enumerList;
	}

}
