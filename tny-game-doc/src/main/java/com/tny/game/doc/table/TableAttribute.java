package com.tny.game.doc.table;

import com.tny.game.doc.*;

import java.util.Map;

public interface TableAttribute {

	void putAttribute(Class<?> clazz, TypeFormatter typeFormatter);

	default String getTemplate() {
		return null;
	}

	default String getOutput() {
		return null;
	}

	Map<String, Object> getContext();

}
