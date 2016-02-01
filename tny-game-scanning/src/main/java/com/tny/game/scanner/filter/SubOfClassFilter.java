package com.tny.game.scanner.filter;

import org.springframework.core.type.classreading.MetadataReader;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SubOfClassFilter implements ClassFilter {

    private Set<Class<?>> includes = new HashSet<>();
    private Set<Class<?>> excludes = new HashSet<>();

    public static final ClassFilter ofInclude(Collection<Class<?>> includes) {
        return new SubOfClassFilter(includes, null);
    }

    public static final ClassFilter ofInclude(Class<?>... includes) {
        return new SubOfClassFilter(Arrays.asList(includes), null);
    }

    public static final ClassFilter ofExclude(Collection<Class<?>> excludes) {
        return new SubOfClassFilter(excludes, null);
    }

    public static final ClassFilter ofExclude(Class<?>... excludes) {
        return new SubOfClassFilter(Arrays.asList(excludes), null);
    }

    public static final ClassFilter of(Collection<Class<?>> includes, Collection<Class<?>> excludes) {
        return new SubOfClassFilter(includes, excludes);
    }

    private SubOfClassFilter(
            Collection<Class<?>> includes,
            Collection<Class<?>> excludes) {
        if (includes != null)
            this.includes.addAll(includes);
        if (excludes != null)
            this.excludes.addAll(excludes);
    }

    @Override
    public boolean include(MetadataReader reader) {
        return ClassFilterHelper.matchSuper(reader, this.includes);
    }

    @Override
    public boolean exclude(MetadataReader reader) {
        return ClassFilterHelper.matchSuper(reader, this.excludes);
    }

}
