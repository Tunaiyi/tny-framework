package com.tny.game.scanner.filter;

import org.springframework.core.type.classreading.MetadataReader;

import java.util.function.Predicate;

public interface ClassIncludeFilter extends ClassFilter {

    static ClassIncludeFilter of(Predicate<MetadataReader> filter) {
        return filter::test;
    }

    @Override
    default boolean exclude(MetadataReader reader) {
        return false;
    }

}
