package com.tny.game.scanner.filter;

import org.springframework.core.type.classreading.MetadataReader;

import java.util.function.Predicate;

public interface ClassExcludeFilter extends ClassFilter {

    static ClassExcludeFilter of(Predicate<MetadataReader> filter) {
        return filter::test;
    }

    @Override
    default boolean include(MetadataReader reader) {
        return true;
    }

}
