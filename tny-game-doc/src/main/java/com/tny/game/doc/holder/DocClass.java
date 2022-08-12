/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc.holder;

import com.google.common.collect.*;
import com.tny.game.common.reflect.*;
import com.tny.game.doc.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/17 03:24
 **/
public class DocClass implements DocClassAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocClass.class);

    private final ClassDoc classDoc;

    private final String docClassName;

    private final String docDesc;

    private final String docText;

    private final Class<?> rawClass;

    private final String rawClassName;

    private final String superClassName;

    private final String packageName;

    private final ListMultimap<String, DocTagValue> tagsMap = LinkedListMultimap.create();

    private final List<DocField> fieldList;

    private final List<DocMethod> methodList;

    private final Map<String, Annotation> annotationMap;

    public static DocClass create(Class<?> clazz) {
        ClassDoc classDoc = clazz.getAnnotation(ClassDoc.class);
        if (classDoc == null) {
            LOGGER.warn("{} is not classDoc", clazz);
            return null;
        }
        return new DocClass(clazz);
    }

    public static <F extends Annotation> DocClass create(Class<?> clazz, Class<F> fieldIdAnnotation, Function<F, Object> fieldIdGetter) {
        ClassDoc classDoc = clazz.getAnnotation(ClassDoc.class);
        if (classDoc == null) {
            LOGGER.warn("{} is not classDoc", clazz);
            return null;
        }
        return new DocClass(clazz, fieldIdAnnotation, fieldIdGetter);
    }

    protected DocClass(Class<?> clazz) {
        this(clazz, null, null);
    }

    protected <F extends Annotation> DocClass(Class<?> clazz, Class<F> fieldIdAnnotation, Function<F, Object> fieldIdGetter) {
        this.rawClass = clazz;
        this.classDoc = clazz.getAnnotation(ClassDoc.class);
        this.rawClassName = clazz.getSimpleName();
        this.packageName = clazz.getPackage().getName();
        var superClass = clazz.getSuperclass();
        if (superClass != null) {
            this.superClassName = superClass.getSimpleName();
        } else {
            this.superClassName = null;
        }
        this.fieldList = Collections.unmodifiableList(createFieldList(clazz, fieldIdAnnotation, fieldIdGetter));
        this.methodList = Collections.unmodifiableList(createMethodList(clazz));
        var tags = clazz.getAnnotation(DocTags.class);
        if (tags != null) {
            for (var tag : tags.value()) {
                var tagValue = new DocTagValue(tag);
                tagsMap.put(tagValue.getTag(), tagValue);
            }
        }
        var docClassName = clazz.getSimpleName();
        var docText = "";
        var docDesc = "";
        if (classDoc != null) {
            if (StringUtils.isNotBlank(classDoc.name())) {
                docClassName = classDoc.name();
            }
            docDesc = classDoc.value();
            docText = classDoc.text();
        }
        this.docClassName = docClassName;
        this.docText = docText;
        this.docDesc = docDesc;
        Map<String, Annotation> annotationMap = new HashMap<>();
        for (var anno : clazz.getAnnotations()) {
            annotationMap.put(anno.annotationType().getSimpleName(), anno);
        }
        this.annotationMap = Collections.unmodifiableMap(annotationMap);
    }

    private static <F extends Annotation> List<DocField> createFieldList(Class<?> clazz,
            Class<F> annotation, Function<F, Object> fieldIdGetter) {
        List<DocField> list = new ArrayList<DocField>();
        for (Field field : ReflectAide.getDeepField(clazz)) {
            DocField fieldDocHolder = DocField.create(field, annotation, fieldIdGetter);
            if (fieldDocHolder != null) {
                list.add(fieldDocHolder);
            }
        }
        return list;
    }

    private static List<DocMethod> createMethodList(Class<?> clazz) {
        List<DocMethod> list = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isBridge()) {
                continue;
            }
            DocMethod holder = DocMethod.create(method);
            if (holder != null) {
                list.add(holder);
            }
        }
        return list;
    }

    @Override
    public Class<?> getRawClass() {
        return rawClass;
    }

    @Override
    public String getRawClassName() {
        return rawClassName;
    }

    @Override
    public String getDocClassName() {
        return docClassName;
    }

    @Override
    public String getDocDesc() {
        return docDesc;
    }

    @Override
    public String getDocText() {
        return docText;
    }

    @Override
    public String getSuperClassName() {
        return superClassName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public ClassDoc getClassDoc() {
        return classDoc;
    }

    @Override
    public List<DocField> getFieldList() {
        return fieldList;
    }

    @Override
    public List<DocMethod> getMethodList() {
        return methodList;
    }

    @Override
    public ListMultimap<String, DocTagValue> getTagValuesMap() {
        return tagsMap;
    }

    @Override
    public boolean isHasTag(String tag) {
        return tagsMap.keySet().contains(tag);
    }

    @Override
    public boolean isMatchAnyTag(Collection<String> tags) {
        return isMatchAnyTag(tags, false);
    }

    @Override
    public boolean isMatchAnyTag(Collection<String> tags, boolean defaultMatch) {
        var keys = tagsMap.keySet();
        if (keys.isEmpty()) {
            return defaultMatch;
        }
        return tags.stream().anyMatch(keys::contains);
    }

    @Override
    public boolean isMatchAllTags(Collection<String> tags) {
        return isMatchAllTags(tags, false);
    }

    @Override
    public boolean isMatchAllTags(Collection<String> tags, boolean defaultMatch) {
        var keys = tagsMap.keySet();
        if (keys.isEmpty()) {
            return defaultMatch;
        }
        return keys.containsAll(tags);
    }

    @Override
    public boolean isMatchAnyValue(String tag, Collection<?> values) {
        return isMatchAnyValue(tag, values, false);
    }

    @Override
    public boolean isMatchAnyValue(String tag, Collection<?> values, boolean defaultMatch) {
        var tagValues = getTagValues(tag)
                .stream()
                .map(DocTagValue::getValue)
                .collect(Collectors.toList());
        if (tagValues.isEmpty()) {
            return defaultMatch;
        }
        return values.stream()
                .map(String::valueOf)
                .anyMatch(tagValues::contains);
    }

    @Override
    public boolean isMatchAllValues(String tag, Collection<?> values) {
        return isMatchAllValues(tag, values, false);
    }

    @Override
    public boolean isMatchAllValues(String tag, Collection<?> values, boolean defaultMatch) {
        var tagValues = getTagValues(tag)
                .stream()
                .map(DocTagValue::getValue)
                .collect(Collectors.toList());
        if (tagValues.isEmpty()) {
            return defaultMatch;
        }
        return values.stream()
                .map(String::valueOf)
                .anyMatch(tagValues::contains);
    }

    @Override
    public boolean isMatch(String tag, Object value) {
        return isMatch(tag, value, false);
    }

    @Override
    public boolean isMatch(String tag, Object value, boolean defaultMatch) {
        var tagValues = getTagValues(tag);
        if (tagValues.isEmpty()) {
            return defaultMatch;
        }
        return tagValues.stream().anyMatch(tv -> Objects.equals(tv.getValue(), String.valueOf(value)));
    }

    @Override
    public List<DocTagValue> getTagValues(String tag) {
        var tagValues = this.tagsMap.get(tag);
        return tagValues.isEmpty() ? Collections.emptyList() : new ArrayList<>(tagValues);
    }

    @Override
    public DocTagValue getTagValue(String tag) {
        var tagValues = this.tagsMap.get(tag);
        return tagValues.isEmpty() ? null : tagValues.get(0);
    }

    @Override
    public Map<String, Annotation> getAnnotationMap() {
        return annotationMap;
    }

    @Override
    public Annotation getAnnotation(String name) {
        return annotationMap.get(name);
    }

}
