package com.tny.game.gradle.doc.plugin

import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.ClassMetadata
import org.springframework.core.type.classreading.MetadataReader

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/6 1:23 下午
 */
class ClassFileMetaSpec {

    private MetadataReader reader

    ClassMetadata getClassMeta() {
        return reader.getClassMetadata()
    }

    AnnotationMetadata getAnnotationMeta() {
        return reader.getAnnotationMetadata()
    }

    String getClassName() {
        return reader.classMetadata.className
    }

    boolean isClass(String className) {
        Objects.equals(reader.classMetadata.className, className)
    }

    boolean hasAnnotation(String annotationClass) {
        reader.getAnnotationMetadata().getAnnotationTypes().contains(annotationClass);
    }

    boolean notHasAnnotation(String annotationClass) {
        !isHasAnnotation(annotationClass)
    }

}
