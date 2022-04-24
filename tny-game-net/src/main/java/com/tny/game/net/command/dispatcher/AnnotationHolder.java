package com.tny.game.net.command.dispatcher;

import com.google.common.collect.*;

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
    private Map<Class<?>, List<Annotation>> annotationMap;

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
