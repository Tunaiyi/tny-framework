package com.tny.game.doc.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.doc.table.*;

import java.util.*;

public class ModuleTableAttribute implements TableAttribute {

	private ModuleConfiger module;

	@JsonIgnore
	private ExportHolder exportHolder;

	public ModuleTableAttribute() {
		super();
	}

	public ModuleTableAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
		super();
		this.module = ModuleConfiger.create(Objects.requireNonNull(ClassDocHolder.create(clazz)), typeFormatter);
		this.exportHolder = ExportHolder.create(clazz);
	}

	@Override
	public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes) {
		this.module = ModuleConfiger.create(Objects.requireNonNull(ClassDocHolder.create(clazz)), typeFormatter);
		this.exportHolder = ExportHolder.create(clazz);
	}

	public ModuleConfiger getModule() {
		return module;
	}

	@Override
	public String getOutput() {
		return this.exportHolder.getOutput();
	}

	@Override
	public Map<String, Object> getContext() {
		return MapBuilder.<String, Object>newBuilder()
				.put("module", module)
				.build();
	}

	@Override
	public String getTemplate() {
		return this.exportHolder.getTemplate();
	}

}
