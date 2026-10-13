/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
