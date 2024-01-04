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
 * @date 2022/7/17 04:26
 **/
public class ClassDescription implements DocClassAccess, Comparable<ClassDescription> {

    private DocClass docClass;

    public ClassDescription() {
    }

    public ClassDescription(DocClass docClass) {
        this.docClass = docClass;
    }

    protected ClassDescription setDocClass(DocClass docClass) {
        this.docClass = docClass;
        return this;
    }

    public DocClass getDocClass() {
        return docClass;
    }

    @Override
    public Class<?> getRawClass() {
        return docClass.getRawClass();
    }

    @Override
    public String getDocClassName() {
        return docClass.getDocClassName();
    }

    @Override
    public String getDocDesc() {
        return docClass.getDocDesc();
    }

    @Override
    public String getDocText() {
        return docClass.getDocText();
    }

    @Override
    public String getRawClassName() {
        return docClass.getRawClassName();
    }

    @Override
    public String getSuperClassName() {
        return docClass.getSuperClassName();
    }

    @Override
    public String getPackageName() {
        return docClass.getPackageName();
    }

    @Override
    public ClassDoc getClassDoc() {
        return docClass.getClassDoc();
    }

    @Override
    public List<DocField> getFieldList() {
        return docClass.getFieldList();
    }

    @Override
    public List<DocMethod> getMethodList() {
        return docClass.getMethodList();
    }

    @Override
    public ListMultimap<String, DocTagValue> getTagValuesMap() {
        return docClass.getTagValuesMap();
    }

    @Override
    public boolean isHasTag(String tag) {
        return docClass.isHasTag(tag);
    }

    @Override
    public boolean isMatchAnyTag(Collection<String> tags) {
        return docClass.isMatchAnyTag(tags);
    }

    @Override
    public boolean isMatchAnyTag(Collection<String> tags, boolean defaultMatch) {
        return docClass.isMatchAnyTag(tags, defaultMatch);
    }

    @Override
    public boolean isMatchAllTags(Collection<String> tags) {
        return docClass.isMatchAllTags(tags);
    }

    @Override
    public boolean isMatchAllTags(Collection<String> tags, boolean defaultMatch) {
        return docClass.isMatchAllTags(tags, defaultMatch);
    }

    @Override
    public boolean isMatchAnyValue(String tag, Collection<?> values) {
        return docClass.isMatchAnyValue(tag, values);
    }

    @Override
    public boolean isMatchAnyValue(String tag, Collection<?> values, boolean defaultMatch) {
        return docClass.isMatchAnyValue(tag, values, defaultMatch);
    }

    @Override
    public boolean isMatchAllValues(String tag, Collection<?> values) {
        return docClass.isMatchAllValues(tag, values);
    }

    @Override
    public boolean isMatchAllValues(String tag, Collection<?> values, boolean defaultMatch) {
        return docClass.isMatchAllValues(tag, values, defaultMatch);
    }

    @Override
    public boolean isMatch(String tag, Object value) {
        return docClass.isMatch(tag, value);
    }

    @Override
    public boolean isMatch(String tag, Object value, boolean defaultMatch) {
        return docClass.isMatch(tag, value, defaultMatch);
    }

    @Override
    public List<DocTagValue> getTagValues(String tag) {
        return docClass.getTagValues(tag);
    }

    @Override
    public DocTagValue getTagValue(String tag) {
        return docClass.getTagValue(tag);
    }

    @Override
    public Map<String, Annotation> getAnnotationMap() {
        return docClass.getAnnotationMap();
    }

    @Override
    public Annotation getAnnotation(String name) {
        return docClass.getAnnotation(name);
    }

    @Override
    public int compareTo(ClassDescription other) {
        int value = this.getPackageName().compareTo(other.getPackageName());
        if (value == 0) {
            return this.getDocClassName().compareTo(other.getDocClassName());
        }
        return value;
    }

}
