package com.tny.game.doc.holder;

import com.google.common.collect.ListMultimap;
import com.tny.game.doc.annotation.*;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/17 04:28
 **/
public interface DocClassAccess {

    Class<?> getRawClass();

    String getDocClassName();

    String getDocText();

    String getClassName();

    String getSuperClassName();

    String getPackageName();

    ClassDoc getClassDoc();

    List<DocVar> getFieldList();

    List<DocMethod> getMethodList();

    ListMultimap<String, DocTagValue> getTagValuesMap();

    boolean isHasTag(String tag);

    boolean isMatchAnyTag(Collection<String> tags);

    boolean isMatchAnyTag(Collection<String> tags, boolean defaultMatch);

    boolean isMatchAllTags(Collection<String> tags);

    boolean isMatchAllTags(Collection<String> tags, boolean defaultMatch);

    boolean isMatchAnyValue(String tag, Collection<?> values);

    boolean isMatchAnyValue(String tag, Collection<?> values, boolean defaultMatch);

    boolean isMatchAllValues(String tag, Collection<?> values);

    boolean isMatchAllValues(String tag, Collection<?> values, boolean defaultMatch);

    boolean isMatch(String tag, Object value);

    boolean isMatch(String tag, Object value, boolean defaultMatch);

    List<DocTagValue> getTagValues(String tag);

    DocTagValue getTagValue(String tag);

    Map<String, Annotation> getAnnotationMap();

    Annotation getAnnotation(String name);

}
