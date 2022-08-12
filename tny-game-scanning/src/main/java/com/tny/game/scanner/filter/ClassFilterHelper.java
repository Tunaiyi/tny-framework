/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.scanner.filter;

import com.tny.game.scanner.*;
import org.slf4j.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.*;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by Kun Yang on 16/1/27.
 */
public interface ClassFilterHelper {

    Logger LOGGER = LoggerFactory.getLogger(ClassFilterHelper.class);

    static ClassIncludeFilter ofInclude(Predicate<MetadataReader> predicate) {
        return predicate::test;
    }

    static ClassExcludeFilter ofExclude(Predicate<MetadataReader> predicate) {
        return predicate::test;
    }

    @SafeVarargs
    static boolean matchAnnotation(MetadataReader reader, Class<? extends Annotation>... annotations) {
        List<Class<? extends Annotation>> annos = Arrays.asList(annotations);
        return matchAnnotation(reader, annos);
    }

    static boolean matchAnnotation(MetadataReader reader, Iterable<Class<? extends Annotation>> annotations) {
        for (Class<? extends Annotation> clazz : annotations) {
            AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();
            Set<String> annotationStrings = annotationMetadata.getAnnotationTypes();
            if (annotationStrings.contains(clazz.getName())) {
                return true;
            }
        }
        return false;
    }

    static boolean matchSuper(MetadataReader reader, Class<?>... classes) {
        return matchSuper(reader, Arrays.asList(classes));
    }

    static Optional<MetadataReader> loadMetadataReader(MetadataReaderFactory factory, String name) throws IOException {
        try {
            MetadataReader reader = factory.getMetadataReader(name);
            return Optional.of(reader);
        } catch (FileNotFoundException e) {
            LOGGER.warn("未找到 {}.class文件", name);
            return Optional.empty();
        }
    }

    static boolean matchSuper(MetadataReader reader, Collection<Class<?>> classes) {
        try {
            String superName = reader.getClassMetadata().getSuperClassName();
            if (classes.stream().anyMatch((c) -> c.getName().equals(superName))) {
                return true;
            }
            List<String> interfaceNames = Arrays.asList(reader.getClassMetadata().getInterfaceNames());
            if (classes.stream().anyMatch((c) -> interfaceNames.contains(c.getName()))) {
                return true;
            }
            MetadataReaderFactory factory = ClassMetadataReaderFactory.getFactory();
            for (String name : interfaceNames) {
                Optional<MetadataReader> interfaceReader = loadMetadataReader(factory, name);
                if (interfaceReader.isPresent() && matchSuper(interfaceReader.get(), classes)) {
                    return true;
                }
            }
            if (superName == null || superName.equals(Object.class.getName())) {
                return false;
            }
            Optional<MetadataReader> superClassReader = loadMetadataReader(factory, superName);
            if (superClassReader.isPresent() && matchSuper(superClassReader.get(), classes)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
