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

package com.tny.game.scanner.filter;

import org.springframework.core.type.classreading.MetadataReader;

import java.lang.annotation.Annotation;
import java.util.*;

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
        if (includes != null) {
            this.includes.addAll(includes);
        }
        if (excludes != null) {
            this.excludes.addAll(excludes);
        }
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
