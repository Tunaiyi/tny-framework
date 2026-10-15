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
package com.tny.game.net.command.dispatcher;

import com.google.common.collect.*;
import org.checkerframework.checker.units.qual.A;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2022/1/6 9:19 PM
 */
public class AnnotationHolder {

    /**
     * 方法注解
     */
    private final Map<Class<?>, List<Annotation>> annotationMap;

    private final List<Annotation> annotations;

    private boolean empty = true;

    public AnnotationHolder(Iterable<Annotation> annotations) {
        Map<Class<? extends Annotation>, List<Annotation>> annotationMap = new HashMap<>();
        for (Annotation annotation : annotations) {
            empty = false;
            annotationMap.computeIfAbsent(annotation.annotationType(), k -> new ArrayList<>()).add(annotation);
        }
        this.annotationMap = toImmutableMap(annotationMap);
        this.annotations = ImmutableList.copyOf(annotations);
    }

    public AnnotationHolder(Annotation[] annotations) {
        Map<Class<? extends Annotation>, List<Annotation>> annotationMap = new HashMap<>();
        for (Annotation annotation : annotations) {
            empty = false;
            annotationMap.computeIfAbsent(annotation.annotationType(), k -> new ArrayList<>()).add(annotation);
        }
        this.annotationMap = toImmutableMap(annotationMap);
        this.annotations = ImmutableList.copyOf(annotations);
    }

    private <T, V> Map<T, List<V>> toImmutableMap(Map<? extends T, List<V>> annotationMap) {
        annotationMap = annotationMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> ImmutableList.copyOf(e.getValue())
                ));
        return ImmutableMap.copyOf(annotationMap);
    }

    public boolean existAnnotation(Class<A> annotationClass) {
        List<Annotation> annotations = this.annotationMap.get(annotationClass);
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        return true;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        List<Annotation> annotations = this.annotationMap.get(annotationClass);
        if (annotations == null || annotations.isEmpty()) {
            return null;
        }
        return as(annotations.get(0));
    }

    public <A extends Annotation> List<A> getAnnotations(Class<A> annotationClass) {
        return as(this.annotationMap.getOrDefault(annotationClass, ImmutableList.of()));
    }

    public Set<Class<?>> getAnnotationClasses() {
        return annotationMap.keySet();
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public boolean isEmpty() {
        return empty;
    }

}
