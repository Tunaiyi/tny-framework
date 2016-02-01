package com.tny.game.scanner.filter;

import org.springframework.core.type.classreading.MetadataReader;

public interface ClassIncludeFilter extends ClassFilter {

    @Override
    default boolean exclude(MetadataReader reader) {
        return false;
    }

}
