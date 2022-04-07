package com.tny.game.doc.dto;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.common.collection.map.*;
import com.tny.game.common.context.*;
import com.tny.game.doc.*;
import com.tny.game.doc.table.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

public class PushDTOTableAttribute implements TableAttribute {

	private PushDTOList pushDTOList = new PushDTOList();

	private Class<Annotation> classAnnotation;

	private Function<Annotation, Object> classIdGetter;

	private Class<Annotation> fieldAnnotation;

	private Function<Annotation, Object> fieldIdGetter;

	public static <C extends Annotation, F extends Annotation> PushDTOTableAttribute create(
			Class<C> classAnnotation, Function<C, Object> classIdGetter,
			Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
		return new PushDTOTableAttribute(classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter);
	}

	public <C extends Annotation, F extends Annotation> PushDTOTableAttribute(
			Class<C> classAnnotation, Function<C, Object> classIdGetter,
			Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
		this.classAnnotation = as(classAnnotation);
		this.classIdGetter = as(classIdGetter);
		this.fieldAnnotation = as(fieldAnnotation);
		this.fieldIdGetter = as(fieldIdGetter);
	}

	@XStreamAlias("pushDTOList")
	private static class PushDTOList {

		@XStreamAsAttribute
		@XStreamAlias("class")
		private String type = "list";

		@XStreamImplicit(itemFieldName = "pushDTO")
		private SortedSet<PushDTOInfo> pushDTOList = new TreeSet<PushDTOInfo>();

	}

	public SortedSet<PushDTOInfo> getPushDTOSet() {
		return Collections.unmodifiableSortedSet(pushDTOList.pushDTOList);
	}

	@Override
	public void putAttribute(Class<?> clazz, TypeFormatter typeFormatter, Attributes attributes) {
		PushDTOInfo info = PushDTOInfo.create(clazz, classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter);
		if (info != null) {
			this.pushDTOList.pushDTOList.add(info);
		}
	}

	@Override
	public Map<String, Object> getContext() {
		return MapBuilder.<String, Object>newBuilder()
				.put("pushDTOList", pushDTOList)
				.build();
	}

}
