package com.tny.game.scanner.filter;

import org.springframework.core.type.classreading.MetadataReader;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AnnotationClassFilter implements ClassFilter {

    private Set<Class<? extends Annotation>> includes = new HashSet<>();
    private Set<Class<? extends Annotation>> excludes = new HashSet<>();

    public static ClassFilter ofInclude(Collection<Class<? extends Annotation>> includes) {
        return new AnnotationClassFilter(includes, null);
    }

    @SafeVarargs
    public static ClassFilter ofInclude(Class<? extends Annotation>... includes) {
        return new AnnotationClassFilter(Arrays.asList(includes), null);
    }

    public static ClassFilter ofExclude(Collection<Class<? extends Annotation>> excludes) {
        return new AnnotationClassFilter(excludes, null);
    }

    @SafeVarargs
    public static ClassFilter ofExclude(Class<? extends Annotation>... excludes) {
        return new AnnotationClassFilter(Arrays.asList(excludes), null);
    }

    public static ClassFilter of(Collection<Class<? extends Annotation>> includes, Collection<Class<? extends Annotation>> excludes) {
        return new AnnotationClassFilter(includes, excludes);
    }

    private AnnotationClassFilter(
            Collection<Class<? extends Annotation>> includes,
            Collection<Class<? extends Annotation>> excludes) {
        if (includes != null)
            this.includes.addAll(includes);
        if (excludes != null)
            this.excludes.addAll(excludes);
    }

    @Override
    public boolean include(MetadataReader reader) {
        return ClassFilterHelper.matchAnnotation(reader, this.includes);
    }

    @Override
    public boolean exclude(MetadataReader reader) {
        return ClassFilterHelper.matchAnnotation(reader, this.excludes);
    }

}
