package com.tny.game.scanner.filter;

import com.tny.game.scanner.ClassScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by Kun Yang on 16/1/27.
 */
public interface ClassFilterHelper {

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
            if (annotationStrings.contains(clazz.getName()))
                return true;
        }
        return false;
    }


    static boolean matchSuper(MetadataReader reader, Class<?>... classes) {
        return matchSuper(reader, Arrays.asList(classes));
    }


    static boolean matchSuper(MetadataReader reader, Collection<Class<?>> classes) {
        try {
            for (Class<?> clazz : classes) {
                String superName = reader.getClassMetadata().getSuperClassName();
                if (classes.stream().anyMatch((c) -> c.getName().equals(superName)))
                    return true;
                List<String> interfaceNames = Arrays.asList(reader.getClassMetadata().getInterfaceNames());
                if (classes.stream().anyMatch((c) -> interfaceNames.contains(c.getName()))) {
                    return true;
                }
                MetadataReaderFactory factory = ClassScanner.getReaderFactory();
                for (String name : interfaceNames) {
                    MetadataReader interfaceReader = factory.getMetadataReader(name);
                    if (matchSuper(interfaceReader, classes))
                        return true;
                }
                if (superName == null || superName.equals(Object.class.getName()))
                    return false;
                MetadataReader superClassReader = factory.getMetadataReader(superName);
                if (matchSuper(superClassReader, classes)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
