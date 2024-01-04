/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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

    String getDocDesc();

    String getDocText();

    String getRawClassName();

    String getSuperClassName();

    String getPackageName();

    ClassDoc getClassDoc();

    List<DocField> getFieldList();

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
