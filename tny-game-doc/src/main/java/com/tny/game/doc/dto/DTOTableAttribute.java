package com.tny.game.doc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.tny.game.common.collection.map.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import com.tny.game.doc.table.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

public class DTOTableAttribute implements TableAttribute {

	private DTOConfig dto;

	@JsonIgnore
	@XStreamOmitField
	private ExportHolder exportHolder;

	@JsonIgnore
	@XStreamOmitField
	private Class<Annotation> classAnnotation;

	@JsonIgnore
	@XStreamOmitField
	private Class<Annotation> fieldAnnotation;

	@JsonIgnore
	@XStreamOmitField
	private Function<Annotation, Object> classIdGetter;

	@JsonIgnore
	@XStreamOmitField
	private Function<Annotation, Object> fieldIdGetter;

	public DTOTableAttribute() {
		super();
	}

	public <C extends Annotation, F extends Annotation> DTOTableAttribute(
			String classAnnotation, Function<C, Object> classIdGetter,
			String fieldAnnotation, Function<F, Object> fieldIdGetter) {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			this.classAnnotation = as(loader.loadClass(classAnnotation));
			this.fieldAnnotation = as(loader.loadClass(fieldAnnotation));
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
		this.classIdGetter = as(classIdGetter);
		this.fieldIdGetter = as(fieldIdGetter);
	}

	public <C extends Annotation, F extends Annotation> DTOTableAttribute(
			Class<C> classAnnotation, Function<C, Object> classIdGetter,
			Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
		this.classAnnotation = as(classAnnotation);
		this.fieldAnnotation = as(fieldAnnotation);
		this.classIdGetter = as(classIdGetter);
		this.fieldIdGetter = as(fieldIdGetter);
	}

	public DTOConfig getDto() {
		return dto;
	}

	@Override
	public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter) {
		this.dto = DTOConfig.create(Objects.requireNonNull(DTODocHolder.create(clazz,
				classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter)), typeFormatter);
		this.exportHolder = ExportHolder.create(clazz);
	}

	@Override
	public String getOutput() {
		return this.exportHolder.getOutput();
	}

	@Override
	public Map<String, Object> getContext() {
		return MapBuilder.<String, Object>newBuilder()
				.put("dto", dto)
				.build();
	}

	@Override
	public String getTemplate() {
		return this.exportHolder.getTemplate();
	}

	@Override
	public String toString() {
		return "DTOTableAttribute{" +
				"dto=" + dto +
				'}';
	}

}
