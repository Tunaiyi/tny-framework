package com.tny.game.scanner.filter;

import org.springframework.core.type.classreading.MetadataReader;

public interface ClassFilter {

    boolean include(MetadataReader reader);

    boolean exclude(MetadataReader reader);

}
