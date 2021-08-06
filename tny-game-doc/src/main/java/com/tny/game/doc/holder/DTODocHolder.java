package com.tny.game.doc.holder;

import com.tny.game.common.reflect.*;
import com.tny.game.doc.annotation.*;
import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class DTODocHolder {

	private static final Logger LOGGER = LoggerFactory.getLogger(DTODocHolder.class);

	private DTODoc dtoDoc;

	private Object id;

	private Class<?> entityClass;

	private List<FieldDocHolder> fieldList;

	private Map<Integer, FieldDocHolder> fieldMap;

	public static <C extends Annotation, F extends Annotation> DTODocHolder create(Class<?> clazz,
			Class<C> classAnnotation, Function<C, Object> classIdGetter,
			Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
		DTODoc dtoDoc = clazz.getAnnotation(DTODoc.class);
		if (dtoDoc == null) {
			LOGGER.error("{} 未添加 {} 注解", clazz, DTODoc.class);
			return null;
		}
		DTODocHolder holder = new DTODocHolder();
		holder.dtoDoc = dtoDoc;
		holder.entityClass = clazz;
		holder.fieldList = Collections.unmodifiableList(createFieldList(clazz, fieldAnnotation, fieldIdGetter));
		C classAnn = clazz.getAnnotation(classAnnotation);
		if (classAnn == null) {
			LOGGER.error("{} 没有 {} annotation", clazz, classAnnotation);
			return null;
		}
		Object id = classIdGetter.apply(classAnn);
		if (id == null) {
			LOGGER.error("{} class id 为 null", clazz);
		}
		holder.id = id;
		return holder;
	}

	private static <F extends Annotation> List<FieldDocHolder> createFieldList(Class<?> clazz,
			Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
		List<FieldDocHolder> list = new ArrayList<FieldDocHolder>();
		for (Field field : ReflectAide.getDeepField(clazz)) {
			FieldDocHolder fieldDocHolder = FieldDocHolder.create(field, fieldAnnotation, fieldIdGetter);
			if (fieldDocHolder != null) {
				list.add(fieldDocHolder);
			}
		}
		return list;
	}

	public DTODoc getDTODoc() {
		return this.dtoDoc;
	}

	public Class<?> getEntityClass() {
		return this.entityClass;
	}

	public Object getId() {
		return this.id;
	}

	public List<FieldDocHolder> getFieldList() {
		return this.fieldList;
	}

}
