package com.tny.game.scanner.filter;

import org.springframework.core.type.classreading.MetadataReader;

public interface ClassExcludeFilter extends ClassFilter {

    @Override
    default boolean include(MetadataReader reader) {
        return true;
    }
}
